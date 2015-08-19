package nl.naturalis.nda.elasticsearch.load;

import nl.naturalis.nda.elasticsearch.load.CSVExtractor.NoSuchFieldException;

import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;

/**
 * @author Ayco Holleman
 *
 */
public class CSVImportUtil {

	private static final Logger logger = Registry.getInstance().getLogger(CSVImportUtil.class);

	private static final String MSG_INVALID_INTEGER = "Invalid integer in field %s: \"%s\" (value set to 0)";
	private static final String MSG_INVALID_NUMBER = "Invalid number in field %s: \"%s\" (value set to 0)";

	private CSVImportUtil()
	{
	}

	/**
	 * Get the whitespace-trimmed value of the {@code fieldNo}<i>th</i> field in
	 * the specified CSV record. Returns {@code null} if the value (after being
	 * whitespace-trimmed) is an empty {@code String}.
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

	/**
	 * Returns the value of the specified field as an integer. This method does
	 * not throw an exception if the field does not contain a valid integer, but
	 * instead issues a warning and returns zero (0). If the field is empty, it
	 * also returns zero.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static int ival(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return 0;
		}
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_INTEGER, fieldNo, s));
			return 0;
		}
	}

	/**
	 * Returns the value of the specified field as a {@code float}. This method
	 * does not throw an exception if the field does not contain a valid
	 * integer, but instead issues a warning and returns zero (0). If the field
	 * is empty, it also returns zero.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static float fval(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return 0;
		}
		try {
			return Float.parseFloat(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_NUMBER, fieldNo, s));
			return 0;
		}
	}

	/**
	 * Returns the value of the specified field as a {@code double}. This method
	 * does not throw an exception if the field does not contain a valid
	 * integer, but instead issues a warning and returns zero (0). If the field
	 * is empty, it also returns zero.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static double dval(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return 0;
		}
		try {
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_NUMBER, fieldNo, s));
			return 0;
		}
	}

	/**
	 * Returns the value of the specified field as a {@code Integer}. This method
	 * does not throw an exception if the field does not contain a valid
	 * integer, but instead issues a warning and returns {@code null}. If the field
	 * is empty, it also returns {@code null}.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static Integer getInteger(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return null;
		}
		try {
			return Integer.valueOf(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_INTEGER, fieldNo, s));
			return null;
		}
	}

	/**
	 * Returns the value of the specified field as a {@code Double}. This method
	 * does not throw an exception if the field does not contain a valid
	 * integer, but instead issues a warning and returns {@code null}. If the field
	 * is empty, it also returns {@code null}.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static Float getFloat(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return null;
		}
		try {
			return Float.valueOf(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_NUMBER, fieldNo, s));
			return null;
		}
	}

	/**
	 * Returns the value of the specified field as a {@code Double}. This method
	 * does not throw an exception if the field does not contain a valid
	 * integer, but instead issues a warning and returns {@code null}. If the field
	 * is empty, it also returns {@code null}.
	 * 
	 * @param record
	 * @param fieldNo
	 * @return
	 */
	public static Double getDouble(CSVRecord record, int fieldNo)
	{
		String s = val(record, fieldNo);
		if (s == null) {
			return null;
		}
		try {
			return Double.valueOf(s);
		}
		catch (NumberFormatException e) {
			logger.warn(String.format(MSG_INVALID_NUMBER, fieldNo, s));
			return null;
		}
	}


}
