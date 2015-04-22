package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsImportAll {

	public static final String SYSPROP_BACKUP = "nl.naturalis.nda.elasticsearch.load.brahms.backup";
	public static final String SYSPROP_BATCHSIZE = "nl.naturalis.nda.elasticsearch.load.brahms.batchsize";
	public static final String SYSPROP_MAXRECORDS = "nl.naturalis.nda.elasticsearch.load.brahms.maxrecords";


	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		try {
			IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importCsvFiles();
		}
		catch (Throwable t) {
			logger.error("Brahms import failed!");
			logger.error(t.getMessage(), t);
		}
		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsImportAll.class);

	private final String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
	
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
	public void importAll() throws Exception
	{
		// Make sure thematic search is configured properly
		ThematicSearchConfig.getInstance();
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.importCsvFiles();
		BrahmsMultiMediaImporter multiMediaImporter = new BrahmsMultiMediaImporter(index);
		multiMediaImporter.importCsvFiles();
		if (backup) {
			File[] files = getCsvFiles();
			for (File f : files) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}
	}


	/**
	 * This method processed dump files one by one and for each imports the
	 * specimens first and then the multimedia.
	 * 
	 * @throws Exception
	 */
	public void importCsvFiles() throws Exception
	{
		//BrahmsDumpUtil.convertFiles();
		ThematicSearchConfig.getInstance();
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] files = getCsvFiles();
		if (files.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		int maxRecords = Integer.parseInt(System.getProperty(SYSPROP_MAXRECORDS, "0"));
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.setMaxRecords(maxRecords);
		BrahmsMultiMediaImporter mediaImporter = new BrahmsMultiMediaImporter(index);
		mediaImporter.setMaxRecords(maxRecords);
		for (File f : files) {
			specimenImporter.importCsv(f.getAbsolutePath());
			mediaImporter.importCsv(f.getAbsolutePath());
			if (backup) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}
		ThematicSearchConfig.getInstance().logMatchInfo();
	}
}
