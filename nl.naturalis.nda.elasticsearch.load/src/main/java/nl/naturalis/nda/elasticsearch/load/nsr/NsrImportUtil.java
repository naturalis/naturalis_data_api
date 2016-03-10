package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.elasticsearch.load.Registry;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.DOMUtil;
import org.w3c.dom.Element;

/**
 * Class providing common functionality for NSR imports.
 * 
 * @author Ayco Holleman
 *
 */
class NsrImportUtil {

	private static final Logger logger = Registry.getInstance().getLogger(NsrImportUtil.class);

	private NsrImportUtil()
	{
	}

	/**
	 * Returns the content of a child element of {@code e}. If there is no child
	 * element with the specified tag name, {@code null} is returned. If the
	 * content contains only whitespace, {@code null} is returned as well.
	 * Otherwise the whitespace trimmed content is returned.
	 * 
	 * @param e
	 * @param childTag
	 * @return
	 */
	static String val(Element e, String childTag)
	{
		String s = DOMUtil.getValue(e, childTag);
		if (s == null)
			return null;
		return (s = s.trim()).length() == 0 ? null : s;
	}

	/**
	 * Returns the XML source files that have not been processed yet.
	 * 
	 * @return
	 */
	static File[] getXmlFiles()
	{
		File dir = getDataDir();
		logger.info("Searching for XML files in " + dir.getAbsolutePath());
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".xml");
			}
		});
	}

	/**
	 * Appends a backup extension ("&#46;imported") to all source files in
	 * the NSR data directory, indicating that they have been processed and
	 * should not be processed again.
	 */
	static void backupXmlFiles()
	{
		logger.info("Creating backups of XML files");
		File[] xmlFiles;
		try {
			xmlFiles = getXmlFiles();
		}
		catch (Exception e) {
			logger.error("Backup failed");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String backupExtension = "." + sdf.format(new Date()) + ".imported";
		for (File xmlFile : xmlFiles) {
			xmlFile.renameTo(new File(xmlFile.getAbsolutePath() + backupExtension));
		}
	}

	/**
	 * Appends a file extension ("&#46;imported") to an NSR source file.
	 */
	static void backupXmlFile(File f)
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
				int pos = file.getName().toLowerCase().indexOf(".xml");
				String chopped = file.getName().substring(0, pos + 4);
				System.out.println(file.getName() + " ---> " + chopped);
				chopped = dir.getAbsolutePath() + "/" + chopped;
				file.renameTo(new File(chopped));
			}
		}
	}

	private static File getDataDir()
	{
		return Registry.getInstance().getConfig().getDirectory("nsr.data_dir");
	}

}
