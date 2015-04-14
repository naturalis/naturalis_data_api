package nl.naturalis.nda.elasticsearch.load;

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

	@SuppressWarnings("serial")
	public static class NoSuchFieldException extends RuntimeException {
		public NoSuchFieldException(CSVRecord record, int fieldNo)
		{
			super(String.format("Specified field number (%s) exceeds number of fields in CSV record(%s)", fieldNo, record.size()));
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);


	/**
	 * Get the value of field {@code fieldNo} in the specified CSV record.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static String val(CSVRecord record, int fieldNo)
	{
		if (fieldNo < record.size()) {
			String s = record.get(fieldNo).trim();
			return s.length() == 0 ? null : s;
		}
		throw new NoSuchFieldException(record, fieldNo);
	}


	public static int ival(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return 0;
		}
		try {
			return Integer.parseInt(val(record, fieldNo));
		}
		catch (NumberFormatException e) {
			logger.warn(String.format("Invalid integer in field %s: \"%s\" (value set to 0)", fieldNo, s));
			return 0;
		}
	}


	public static double dval(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return 0;
		}
		try {
			return Double.parseDouble(val(record, fieldNo));
		}
		catch (NumberFormatException e) {
			logger.warn(String.format("Invalid number in field %s: \"%s\" (value set to 0)", fieldNo, s));
			return 0;
		}
	}

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
	 * Character set used in the CSV file
	 */
	protected Charset charset = Charset.forName("UTF-8");

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
		logger.info(String.format("[%s] Processing CSV file \"%s\"", getClass().getSimpleName(), path));
		if (csvFormat == null) {
			csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter);
		}
		//format = format.withRecordSeparator("\r\n");

		/*
		 * Make sure default encoding is UTF-8. The main reason we want this to
		 * be the case is that CSVParser.parse(String, CSVFormat) parses the
		 * String using the default encoding; you cannot specify an arbitrary
		 * encoding (sad but true). Just before we pass a line to
		 * CSVParser.parse, we make sure it is UTF8-encoded, thus the default
		 * encoding HAS to be UTF-8. However, it might anyhow be a good idea to
		 * do this check for all import programs. Finally, note that for the
		 * CSVParser it actually probably doesn't really matter whether it gets
		 * UTF-8, ISO-8995-1 or Cp1252, because all delimiters (end-of-field,
		 * end-of-record) are probably encoded identically in all of these
		 * character sets. Nevertheless, we JUST WANT THINGS TO BE UTF-8.
		 */
		Charset utf8 = Charset.forName("UTF-8");
		if (!Charset.defaultCharset().equals(utf8)) {
			logger.error("Invalid default character encoding: " + Charset.defaultCharset().name());
			logger.error(getClass().getSimpleName() + " can only run with UTF-8 as default character encoding");
			logger.error("Please add the following command line argument when running " + getClass().getSimpleName() + ": -Dfile.encoding=UTF-8");
			logger.error("Program aborted");
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
					if (logger.isDebugEnabled()) {
						logger.debug("Ignoring empty line: " + lineNo);
					}
					continue;
				}
				++processed;
				try {
					if (!charset.equals(utf8)) {
						line = new String(line.getBytes(utf8));
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
					if (suppressErrors) {
						if (logger.isDebugEnabled()) {
							logger.debug("Error at line " + lineNo + ": " + t.getMessage());
						}
					}
					else {
						logger.error("Error at line " + lineNo + ": " + t.getMessage());
					}
					if (logger.isDebugEnabled()) {
						logger.debug(line);
						//logger.debug("Stack trace: ", t);
					}
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger.info(String.format("[%s] Records processed: %s", getClass().getSimpleName(), processed));
					logger.info(String.format("[%s] Documents indexed: %s", getClass().getSimpleName(), indexed));
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
		logger.info("Records processed: " + processed);
		logger.info("Records skipped: " + skipped);
		logger.info("Bad records: " + bad);
		logger.info("Documents indexed: " + indexed);
		logger.info(String.format("[%s] Finished processing file: %s", getClass().getSimpleName(), path));
	}


	@SuppressWarnings({ "static-method" })
	protected boolean skipRecord(CSVRecord record)
	{
		return false;
	}


	protected abstract List<T> transfer(CSVRecord record, String csvRecord, int lineNo) throws Exception;


	@SuppressWarnings({ "static-method" })
	protected List<String> getIds(CSVRecord record)
	{
		return null;
	}


	@SuppressWarnings({ "static-method" })
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
