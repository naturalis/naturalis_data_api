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
	private static final int DEFAULT_BATCH_SIZE = 1000;

	private final Index index;
	private final String type;

	private int batchSize = DEFAULT_BATCH_SIZE;
	private boolean specifyId = false;
	private boolean specifyParent = false;


	public CSVImporter(Index index, String type)
	{
		this.index = index;
		this.type = type;
	}


	protected static int getInt(CSVRecord record, int fieldNo)
	{
		String s = record.get(fieldNo);
		if (s.trim().length() == 0) {
			return 0;
		}
		return Integer.parseInt(s);
	}


	public void importCsv(String path) throws IOException
	{
		logger.info(String.format("Processing CSV file \"%s\"", path));
		CSVFormat format = CSVFormat.DEFAULT;
		format = format.withDelimiter('\t');
		LineNumberReader lnr = new LineNumberReader(new FileReader(path));
		int processed = 0;
		String line;
		try {
			lnr.readLine(); // Skip header
			List<T> objects = new ArrayList<T>(batchSize);
			List<String> ids = specifyId ? new ArrayList<String>(batchSize) : null;
			List<String> parentIds = specifyParent ? new ArrayList<String>(batchSize) : null;
			while ((line = lnr.readLine()) != null) {
				if (++processed % 50000 == 0) {
					logger.info("Records processed: " + processed);
				}
				if (line.trim().length() == 0) {
					logger.info("Skipping empty line: " + processed);
				}
				try {
					CSVRecord record = CSVParser.parse(line, format).iterator().next();
					T object = transfer(record);
					objects.add(object);
					if (specifyId) {
						ids.add(getId(record));
					}
					if (specifyParent) {
						parentIds.add(getParentId(record));
					}
					if (objects.size() == batchSize) {
						index.saveObjects(type, objects, null, parentIds);
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
					logger.error("Error at line " + processed, t);
				}
			}
			if (!objects.isEmpty()) {
				index.saveObjects(type, objects, ids, parentIds);
			}
		}
		finally {
			lnr.close();
		}
		logger.info("Ready");

	}


	protected abstract T transfer(CSVRecord record);


	@SuppressWarnings({ "static-method", "unused" })
	protected String getId(CSVRecord record)
	{
		return null;
	}


	@SuppressWarnings({ "static-method", "unused" })
	protected String getParentId(CSVRecord record)
	{
		return null;
	}


	public int getBatchSize()
	{
		return batchSize;
	}


	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
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
