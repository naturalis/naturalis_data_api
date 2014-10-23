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

	@SuppressWarnings("serial")
	public static class NoSuchFieldException extends RuntimeException {
		public NoSuchFieldException(CSVRecord record, int fieldNo)
		{
			super(String.format("Specified field number (%s) exceeds number of fields in CSV record(%s)", fieldNo, record.size()));
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);


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

	private final Index index;
	private final String type;

	private int bulkRequestSize = 1000;
	private int maxRecords = 0;
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

			++lineNo;
			lnr.readLine(); // Skip header; TODO: make configurable

			while ((line = lnr.readLine()) != null) {
				++lineNo;
				if (line.trim().length() == 0) {
					logger.info("Ignoring empty line: " + lineNo);
					continue;
				}
				++processed;
				try {
					record = CSVParser.parse(line, format).iterator().next();
					if (skipRecord(record)) {
						++skipped;
					}
					else {
						objects.addAll(transfer(record));
						if (specifyId) {
							ids.addAll(getIds(record));
						}
						if (specifyParent) {
							parentIds.addAll(getParentIds(record));
						}
						if (objects.size() >= bulkRequestSize) {
							index.saveObjects(type, objects, ids, parentIds);
							indexed += objects.size();
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
				catch (Throwable t) {
					++bad;
					logger.error("Error at line " + lineNo + ": " + t.getMessage());
					logger.error("Line: [[" + line + "]]");
					logger.debug("Stack trace: ", t);
				}
				if (maxRecords > 0 && processed >= maxRecords) {
					break;
				}
				if (processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
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
		logger.info(getClass().getSimpleName() + " finished");
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


	public int getMaxRecords()
	{
		return maxRecords;
	}


	public void setMaxRecords(int maxRecords)
	{
		this.maxRecords = maxRecords;
	}

}
