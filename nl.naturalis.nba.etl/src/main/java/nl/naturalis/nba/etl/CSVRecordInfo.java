package nl.naturalis.nba.etl;

import nl.naturalis.nba.etl.CSVExtractor.NoSuchFieldException;

import org.apache.commons.csv.CSVRecord;

/**
 * Immutable class wrapping a commons-csv {@link CSVRecord} instance.
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            An enum class whose constants symbolize the fields in the CSV
 *            record. In other words the first constant symbolizes the first CSV
 *            field, the second constant symbolizes the second CSV field, etc.
 *            The enum class must have exactly as many constants as the number
 *            of CSV fields in the CSV record.
 */
public final class CSVRecordInfo<T extends Enum<T>> {

	private final CSVRecord record;
	private final String line;
	private final int lineNumber;

	public CSVRecordInfo(CSVRecord record, String line, int lineNumber)
	{
		this.record = record;
		this.line = line;
		this.lineNumber = lineNumber;
	}

	/**
	 * Returns the value of the specified CSV field. Equivalent to calling
	 * {@code get(field, true)}.
	 * 
	 * @param field
	 * @return
	 */
	public String get(T field)
	{
		return get(field, true);
	}

	/**
	 * Returns the value of the specified CSV field.
	 * 
	 * @param field
	 *            The CSV field corresponding to the ordinal value of the
	 *            specified enum constant.
	 * @param emptyIsNull
	 *            If {@code true}, the value of the field is whitespace-trimmed
	 *            and, if a zero-length string remains, {@code null} is
	 *            returned, otherwise the whitespace-trimmed string. If
	 *            {@code false}, the value of the field is returned as-is.
	 * @return
	 */
	public String get(T field, boolean emptyIsNull)
	{
		int fieldNo = field.ordinal();
		if (fieldNo < record.size()) {
			if (emptyIsNull) {
				String s = record.get(fieldNo).trim();
				return s.length() == 0 ? null : s;
			}
			return record.get(fieldNo);
		}
		throw new NoSuchFieldException(record, fieldNo);
	}

	/**
	 * Returns the raw line within the CSV file from which this instance was
	 * created.
	 * 
	 * @return
	 */
	public String getLine()
	{
		return line;
	}

	/**
	 * Returns the line number of the line from which this instance was created.
	 * 
	 * @return
	 */
	public int getLineNumber()
	{
		return lineNumber;
	}

}
