package nl.naturalis.nda.elasticsearch.load;

import org.apache.commons.csv.CSVRecord;

public class CSVRecordInfo {

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

	public String getLine()
	{
		return line;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

}
