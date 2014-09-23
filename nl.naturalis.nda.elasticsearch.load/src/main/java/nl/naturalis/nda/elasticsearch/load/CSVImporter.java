package nl.naturalis.nda.elasticsearch.load;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.Index;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CSVImporter<T> {

	private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);


	protected static int getInt(CSVRecord record, int fieldNo)
	{
		String s = record.get(fieldNo);
		if (s.trim().length() == 0) {
			return 0;
		}
		return Integer.parseInt(s);
	}

	private final Index index;
	private final String type;

	private int bulkRequestSize = 1000;
	private boolean specifyId = false;
	private boolean specifyParent = false;

	protected char delimiter = '\t';


	public CSVImporter(Index index, String type)
	{
		this.index = index;
		this.type = type;
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter(delimiter);
		format = format.withRecordSeparator("\r\n");
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));

		int processed = 0;
		int skipped = 0;
		int bad = 0;

		List<T> objects = new ArrayList<T>(bulkRequestSize);
		List<String> ids = specifyId ? new ArrayList<String>(bulkRequestSize) : null;
		List<String> parentIds = specifyParent ? new ArrayList<String>(bulkRequestSize) : null;

		String line;
		CSVRecord record;

		try {
			lnr.readLine(); // Skip header		

			while ((line = lnr.readLine()) != null) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + (processed + 1));
				}
				try {
					record = CSVParser.parse(line, format).iterator().next();
					if (skipRecord(record)) {
						++skipped;
						continue;
					}
					objects.addAll(transfer(record));
					if (specifyId) {
						ids.addAll(getIds(record));
					}
					if (specifyParent) {
						parentIds.addAll(getParentIds(record));
					}
					if (objects.size() >= bulkRequestSize) {
						index.saveObjects(type, objects, ids, parentIds);
						objects.clear();
						if (specifyId) {
							ids.clear();
						}
						if (specifyParent) {
							parentIds.clear();
						}
					}
				}
				catch (Throwable t) {
					++bad;
					logger.debug(line, t);
					logger.error("Error at line " + (processed + 1) + ": " + t.getMessage());

				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(type, objects, ids, parentIds);
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Records processed: " + processed);
		logger.info("Records skipped: " + skipped);
		logger.info("Bad records: " + bad);
		logger.info("Ready");

	}


	@SuppressWarnings({ "static-method", "unused" })
	protected boolean skipRecord(CSVRecord record)
	{
		return false;
	}


	protected abstract List<T> transfer(CSVRecord record) throws Exception;


	@SuppressWarnings({ "static-method", "unused" })
	protected List<String> getIds(CSVRecord record)
	{
		return null;
	}


	@SuppressWarnings({ "static-method", "unused" })
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

}
