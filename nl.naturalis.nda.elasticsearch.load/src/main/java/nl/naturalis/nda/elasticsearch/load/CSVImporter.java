package nl.naturalis.nda.elasticsearch.load;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.Index;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CSVImporter<T> {

	public static class NoSuchFieldException extends RuntimeException {
		static final String MSG = "Specified field number (%s) exceeds number of fields in CSV record(%s)";
		public NoSuchFieldException(CSVRecord record, int fieldNo)
		{
			super(String.format(MSG, fieldNo, record.size()));
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);

	protected final Index index;
	protected final String type;

	private int bulkRequestSize = 1000;
	private int maxRecords = 0;
	private boolean specifyId = false;
	private boolean specifyParent = false;

	/*
	 * Is first line in CSV file a header containing field names?
	 */
	protected boolean skipHeader = true;
	protected CSVFormat csvFormat;
	protected char delimiter = '\t';
	/*
	 * Character set used in the CSV file. By default assumed to be UTF-8
	 */
	protected Charset charset = UTF_8;

	/*
	 * Will log errors as debug messages. Specifically useful when expecting
	 * huge amounts of errors, as with Brahms, and logging itself becomes a
	 * performance drain.
	 */
	protected boolean suppressErrors = false;


	public CSVImporter(Index index, String type)
	{
		this.index = index;
		this.type = type;
	}


	public void importCsv(String path) throws IOException
	{
		logger().info(String.format("Processing CSV file \"%s\"", path));
		if (csvFormat == null) {
			csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter);
		}

		/*
		 * Make sure default encoding is UTF-8. The main reason we want this to
		 * be the case is that CSVParser.parse(String, CSVFormat) parses the
		 * String using the default encoding. You cannot specify an arbitrary
		 * encoding. Sad but true. Just before we pass a line to
		 * CSVParser.parse, we make sure it is UTF8-encoded, thus the default
		 * encoding HAS to be UTF-8. Note that for the CSVParser itself, it
		 * doesn't really matter whether it gets UTF-8, ISO-8995-1 or Cp1252,
		 * because all delimiters (end-of-field, end-of-record) are encoded
		 * identically in all of these character sets. Thus, tokenizing will not
		 * be a problem. Nevertheless, we JUST WANT THINGS TO BE UTF-8 ACROSS
		 * THE BOARD.
		 */
		if (!Charset.defaultCharset().equals(UTF_8)) {
			logger().error("Invalid default character encoding: " + Charset.defaultCharset().name());
			logger().error(getClass().getSimpleName() + " can only run with a default character encoding of UTF-8");
			logger().error("Please add the following command line argument when running " + getClass().getSimpleName() + ": -Dfile.encoding=UTF-8");
			logger().error("Program aborted");
			return;
		}

		InputStreamReader isr = new InputStreamReader(new FileInputStream(path), charset);
		LineNumberReader lnr = new LineNumberReader(isr);

		int lineNo = 0;
		int processed = 0;
		int indexed = 0;
		int skipped = 0;
		int bad = 0;

		List<T> objects = new ArrayList<T>(bulkRequestSize);
		List<String> ids = specifyId ? new ArrayList<String>(bulkRequestSize) : null;
		List<String> parentIds = specifyParent ? new ArrayList<String>(bulkRequestSize) : null;

		String line;
		CSVRecord record;

		try {

			if (skipHeader) {
				++lineNo;
				lnr.readLine();
			}

			while ((line = lnr.readLine()) != null) {
				++lineNo;
				if (line.trim().length() == 0) {
					if (logger().isDebugEnabled()) {
						logger().debug("Ignoring empty line: " + lineNo);
					}
					continue;
				}
				++processed;
				try {
					if (!charset.equals(UTF_8)) {
						line = new String(line.getBytes(UTF_8));
					}
					record = CSVParser.parse(line, csvFormat).iterator().next();
					if (skipRecord(record)) {
						++skipped;
					}
					else {
						List<T> extracted = transfer(record, line, lineNo);
						if (extracted == null || extracted.size() == 0) {
							continue;
						}
						objects.addAll(extracted);
						if (specifyId) {
							ids.addAll(getIds(record));
						}
						if (specifyParent) {
							parentIds.addAll(getParentIds(record));
						}
						if (objects.size() >= bulkRequestSize) {
							try {
								index.saveObjects(type, objects, ids, parentIds);
								indexed += objects.size();
							}
							finally {
								objects.clear();
								if (specifyId) {
									ids.clear();
								}
								if (specifyParent) {
									parentIds.clear();
								}
							}
						}
					}
				}
				catch (Throwable t) {
					++bad;
					if (!suppressErrors) {
						logger().error("Error at line " + lineNo + ": " + t.getMessage());
						logger().error(line);
					}
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger().info(String.format("Records processed: %s", processed));
					logger().info(String.format("Documents indexed: %s", indexed));
				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(type, objects, ids, parentIds);
				indexed += objects.size();
			}
		}
		finally {
			lnr.close();
		}
		logger().info("Records processed: " + processed);
		logger().info("Records skipped: " + skipped);
		logger().info("Bad records: " + bad);
		logger().info("Documents indexed: " + indexed);
		logger().info(String.format("Finished processing file: %s", path));
	}

	/**
	 * Subclasses may provided their own logger, so it's more clear what type of
	 * datasource is being processed.
	 * 
	 * @return
	 */
	protected Logger logger()
	{
		return logger;
	}


	@SuppressWarnings({ "unused" })
	protected boolean skipRecord(CSVRecord record)
	{
		return false;
	}


	protected abstract List<T> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception;


	@SuppressWarnings({ "unused" })
	protected List<String> getIds(CSVRecord record)
	{
		return null;
	}


	@SuppressWarnings({ "unused" })
	protected List<String> getParentIds(CSVRecord record)
	{
		return null;
	}


	public int getBulkRequestSize()
	{
		return bulkRequestSize;
	}


	public void setBulkRequestSize(int bulkRequestSize)
	{
		this.bulkRequestSize = bulkRequestSize;
	}


	public boolean isSpecifyId()
	{
		return specifyId;
	}


	public void setSpecifyId(boolean specifyId)
	{
		this.specifyId = specifyId;
	}


	public boolean isSpecifyParent()
	{
		return specifyParent;
	}


	public void setSpecifyParent(boolean specifyParent)
	{
		this.specifyParent = specifyParent;
	}


	public int getMaxRecords()
	{
		return maxRecords;
	}


	public void setMaxRecords(int maxRecords)
	{
		this.maxRecords = maxRecords;
	}

}
