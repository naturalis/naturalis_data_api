package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.slf4j.Logger;

public class BrahmsImportAll {

	public static void main(String[] args)
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importAllPerFile();
		}
		catch (Throwable t) {
			logger.error("Brahms import failed!");
			logger.error(t.getMessage(), t);
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	public static final String ID_PREFIX = "BRAHMS-";
	public static final String SYSPROP_BACKUP = "nl.naturalis.nda.elasticsearch.load.brahms.backup";
	public static final String SYSPROP_BATCHSIZE = "nl.naturalis.nda.elasticsearch.load.brahms.batchsize";
	public static final String SYSPROP_MAXRECORDS = "nl.naturalis.nda.elasticsearch.load.brahms.maxrecords";


	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportAll.class);

	private final IndexNative index;
	private final boolean backup;


	public BrahmsImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty(SYSPROP_BACKUP, "1");
		backup = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	/**
	 * This method first imports all specimens, then all multimedia.
	 * 
	 * @throws Exception
	 */
	public void importAllPerType() throws Exception
	{
		// Make sure thematic search is configured properly
		ThemeCache.getInstance();
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.importCsvFiles();
		BrahmsMultiMediaImporter multiMediaImporter = new BrahmsMultiMediaImporter(index);
		multiMediaImporter.importCsvFiles();
		if (backup) {
			String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
			for (File f : getCsvFiles()) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}
	}


	/**
	 * For each XML file, first import all specimens contained in it, then all
	 * multimedia.
	 * 
	 * @throws Exception
	 */
	public void importAllPerFile() throws Exception
	{
		ThemeCache.getInstance();
		File[] files = getCsvFiles();
		if (files.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		int maxRecords = Integer.parseInt(System.getProperty(SYSPROP_MAXRECORDS, "0"));
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.setMaxRecords(maxRecords);
		BrahmsMultiMediaImporter mediaImporter = new BrahmsMultiMediaImporter(index);
		mediaImporter.setMaxRecords(maxRecords);
		for (File f : files) {
			specimenImporter.importCsv(f.getAbsolutePath());
			mediaImporter.importCsv(f.getAbsolutePath());
		}
		if (backup) {
			String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
			for (File f : getCsvFiles()) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}
		ThemeCache.getInstance().logMatchInfo();
	}
}
