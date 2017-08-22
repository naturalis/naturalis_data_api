package nl.naturalis.nba.etl.ndff;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nba.dao.DaoRegistry;

class NdffImportUtil {

	private static final SimpleDateFormat fileNameDateFormatter = new SimpleDateFormat("yyyyMMdd");

	private NdffImportUtil()
	{
	}

	/**
	 * Creates a backup of successfully processed CSV files by appending a
	 * datetime stamp and a {@code .imported} file extension to their name.
	 */
	static void backup()
	{
		String ext = "." + fileNameDateFormatter.format(new Date()) + ".imported";
		for (File f : getCsvFiles()) {
			f.renameTo(new File(f.getAbsolutePath() + ext));
		}
	}

	/**
	 * Removes the {@code .imported} file extension from files that have it,
	 * causing them to be re-processed the next time an import is started.
	 */
	static void removeBackupExtension()
	{
		File dir = getDataDir();
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".imported");
			}
		});
		for (File file : files) {
			int pos = file.getName().toLowerCase().indexOf(".csv");
			String chopped = file.getName().substring(0, pos + 4);
			System.out.println(file.getName() + " ---> " + chopped);
			chopped = dir.getAbsolutePath() + "/" + chopped;
			file.renameTo(new File(chopped));
		}
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
		return DaoRegistry.getInstance().getConfiguration().getDirectory("ndff.data.dir");
	}
}
