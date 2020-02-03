package nl.naturalis.nba.etl.nsr;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Class providing common functionality for NSR imports.
 * 
 * @author Ayco Holleman
 *
 */
class NsrImportUtil {

	private static final Logger logger = ETLRegistry.getInstance().getLogger(NsrImportUtil.class);

	private NsrImportUtil()
	{
	}

	/**
	 * Returns the content of a child element of {@code e}. If there is no child
	 * element with the specified tag name, {@code null} is returned. If the
	 * content contains only whitespace, {@code null} is returned as well.
	 * Otherwise the whitespace trimmed content is returned.
	 * 
	 * @param jsonNode json string
	 * @param childTag child tag as String
	 * @return the String value of the child element
	 */
	static String val(JsonNode jsonNode, String childTag)
	{
		String s = jsonNode.get(childTag).asText();
		if (s == null)
			return null;
		return (s = s.trim()).length() == 0 ? null : s;
	}

	static String val(String str)
	{
		if (str == null)
			return null;
		return (str = str.trim()).length() == 0 ? null : str;
	}


	/**
	 * Returns the JSON source files that have not been processed yet.
	 * 
	 * @return File[] fileNames
	 */
	static File[] getJsonFiles()
	{
		File dir = getDataDir();
		logger.info("Searching for JSON files in " + dir.getAbsolutePath());
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".jsonl");
			}
		});
	}

	/**
	 * Appends a backup extension ("&#46;imported") to all source files in
	 * the NSR data directory, indicating that they have been processed and
	 * should not be processed again.
	 */
	static void backupJsonFiles()
	{
		logger.info("Creating backups of JSON files");
		File[] jsonFiles;
		try {
			jsonFiles = getJsonFiles();
		}
		catch (Exception e) {
			logger.error("Backup failed");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String backupExtension = "." + sdf.format(new Date()) + ".imported";
		for (File jsonFile : jsonFiles) {
			jsonFile.renameTo(new File(jsonFile.getAbsolutePath() + backupExtension));
		}
	}

	/**
	 * Appends a file extension ("&#46;imported") to an NSR source file.
	 */
	static void backupJsonFile(File f)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String backupExtension = "." + sdf.format(new Date()) + ".imported";
		f.renameTo(new File(f.getAbsolutePath() + backupExtension));
	}

	/**
	 * Removes the backup extension ("&#46;imported") from all source files
	 * in the NSR data directory. Nice for repetitive testing. Not for
	 * production purposes.
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
		if (files.length == 0) {
			logger.info("No backup files found");
		}
		else {
			for (File file : files) {
				int pos = file.getName().toLowerCase().indexOf(".jsonl");
				String chopped = file.getName().substring(0, pos + 6);
				logger.info(file.getName() + " ---> " + chopped);
				chopped = dir.getAbsolutePath() + "/" + chopped;
				file.renameTo(new File(chopped));
			}
		}
	}

	private static File getDataDir()
	{
		return DaoRegistry.getInstance().getConfiguration().getDirectory("nsr.data.dir");
	}

}
