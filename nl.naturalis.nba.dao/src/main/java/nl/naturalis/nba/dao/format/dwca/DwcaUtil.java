package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.File;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.utils.FileUtil;

/**
 * Utility class for the DwCA creation process.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaUtil {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(DwcaUtil.class);

	private DwcaUtil()
	{
	}

	/**
	 * Returns the directory containing the configuration files for the
	 * specified type of data sets.
	 */
	public static File getDwcaConfigurationDirectory(DwcaDataSetType dataSetType)
	{
		File root = DaoRegistry.getInstance().getConfigurationDirectory();
		String dirName = dataSetType.name().toLowerCase();
		return FileUtil.newFile(root, "dwca/" + dirName);
	}

}
