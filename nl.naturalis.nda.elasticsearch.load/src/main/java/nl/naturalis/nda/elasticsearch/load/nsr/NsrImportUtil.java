package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

public class NsrImportUtil {

	private static final Logger logger = LoggerFactory.getLogger(NsrImportUtil.class);


	private NsrImportUtil()
	{
	}


	static File[] getXMLFiles() throws Exception
	{
		File dir = LoadUtil.getConfig().getDirectory("nsr.xml_dir");
		logger.info("Searching for new XML files in " + dir.getAbsolutePath());
		return dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".xml");
			}
		});
	}


	static void backupXMLFiles()
	{
		NsrImportAll.logger.info("Creating backups of XML files");
		File[] xmlFiles;
		try {
			xmlFiles = getXMLFiles();
		}
		catch (Exception e) {
			NsrImportAll.logger.error("Backup failed");
			return;
		}
		String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
		for (File xmlFile : xmlFiles) {
			xmlFile.renameTo(new File(xmlFile.getAbsolutePath() + backupExtension));
		}
	}

}
