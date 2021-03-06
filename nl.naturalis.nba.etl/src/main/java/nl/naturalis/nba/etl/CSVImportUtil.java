package nl.naturalis.nba.etl;

import static nl.naturalis.nba.utils.StringUtil.lpad;
import static nl.naturalis.nba.utils.StringUtil.rpad;

import org.apache.logging.log4j.Logger;


/**
 * 
 * Utility class for CSV import programs.
 * 
 * @author Ayco Holleman
 *
 */
public class CSVImportUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = ETLRegistry.getInstance().getLogger(CSVImportUtil.class);

	private CSVImportUtil()
	{
	}
	
	/**
	 * Returns the default prefix for messages written to the log file.
	 * 
	 * @param lineNo The line number within the CSV file
	 * @param objectID The ID of the object
	 * @return
	 */
	public static String getDefaultMessagePrefix(long lineNo, String objectID)
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}
