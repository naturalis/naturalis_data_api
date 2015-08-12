package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

class NsrImportUtil {

	private static final Logger logger = LoggerFactory.getLogger(NsrImportUtil.class);
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");


	private NsrImportUtil()
	{
	}


	static File[] getXmlFiles() throws Exception
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
		String backupExtension = "." + sdf.format(new Date()) + ".imported";
		for (File xmlFile : xmlFiles) {
			xmlFile.renameTo(new File(xmlFile.getAbsolutePath() + backupExtension));
		}
	}


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
			int pos = file.getName().toLowerCase().indexOf(".xml");
			String chopped = file.getName().substring(0, pos + 4);
			System.out.println(file.getName() + " ---> " + chopped);
			chopped = dir.getAbsolutePath() + "/" + chopped;
			file.renameTo(new File(chopped));
		}
	}

	private static File getDataDir()
	{
		return LoadUtil.getConfig().getDirectory("nsr.xml_dir");
	}

}
