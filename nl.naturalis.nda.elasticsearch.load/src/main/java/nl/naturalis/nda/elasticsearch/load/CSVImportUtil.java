package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.lpad;
import static org.domainobject.util.StringUtil.rpad;

import org.slf4j.Logger;

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

	public static String getDefaultMessagePrefix(int lineNo, String objectID)
	{
		return "Line " + lpad(lineNo, 6, '0', " | ") + rpad(objectID, 16, " | ");
	}

}
