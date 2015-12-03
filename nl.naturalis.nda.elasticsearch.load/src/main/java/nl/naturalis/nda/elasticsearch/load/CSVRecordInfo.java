package nl.naturalis.nda.elasticsearch.load;

import nl.naturalis.nda.elasticsearch.load.CSVExtractor.NoSuchFieldException;

import org.apache.commons.csv.CSVRecord;

/**
 * A java bean encapsulating a commons-csv {@link CSVRecord} instance, the line
 * within the CSV file from which it was created, and the line number of the
 * line. {@link CSVTransformer}s receive {@code CSVRecordInfo} instances
 * rather than just {@code CSVRecord} instances for improved error reporting.
 * 
 * @author Ayco Holleman
 *
 */
public class CSVRecordInfo<T extends Enum<T>> {

	private CSVRecord record;
	private String line;
	private int lineNumber;

	public CSVRecordInfo(CSVRecord record, String line, int lineNumber)
	{
		this.record = record;
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public CSVRecord getRecord()
	{
		return record;
	}
	
	public String get(T field) {
		int fieldNo = field.ordinal();
		if (fieldNo < record.size()) {
			String s = record.get(fieldNo).trim();
			return s.length() == 0 ? null : s;
		}
		throw new NoSuchFieldException(record, fieldNo);	
	}

	public String getLine()
	{
		return line;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

}
