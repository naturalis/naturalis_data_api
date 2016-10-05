package nl.naturalis.nba.etl.geo;

import java.io.File;
import java.io.FilenameFilter;

import nl.naturalis.nba.dao.DaoRegistry;

class GeoImportUtil {

	private GeoImportUtil()
	{
	}

	/**
	 * Provides a list of CSV files to process. Only files whose name end with
	 * {@code .csv} (case-insensitive) will be processed.
	 * 
	 * @return
	 */
	static File[] getCsvFiles()
	{
		File[] files = getDataDir().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		return files;
	}

	private static File getDataDir()
	{
		return DaoRegistry.getInstance().getConfiguration().getDirectory("geo.data.dir");
	}
}
