package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

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
	private static final Logger logger = Registry.getInstance().getLogger(CSVImportUtil.class);

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
	public static String getDefaultMessagePrefix(int lineNo, String objectID)
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}
