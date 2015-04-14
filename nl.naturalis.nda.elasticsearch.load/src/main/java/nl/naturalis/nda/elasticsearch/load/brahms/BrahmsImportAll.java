package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.File;
import java.io.FilenameFilter;

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

		// Check thematic search is configured properly
		ThematicSearchConfig.getInstance();

		IndexNative index = null;

		try {
			index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importAll();
		}
		catch (Throwable t) {
			logger.error("Brahms import Failed!");
			logger.error(t.getMessage(), t);
		}
		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsImportAll.class);

	private final IndexNative index;
	private final boolean backup;


	public BrahmsImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty(SYSPROP_BACKUP, "1");
		backup = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importAll() throws Exception
	{
		// Make sure thematic search is configured properly by
		// instantiating the one & only instance.
		ThematicSearchConfig.getInstance();
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.importCsvFiles();
		BrahmsMultiMediaImporter multiMediaImporter = new BrahmsMultiMediaImporter(index);
		multiMediaImporter.importCsvFiles();
		if (backup) {
			BrahmsDumpUtil.backup();
		}
	}


	public void importCsvFiles() throws Exception
	{
		BrahmsDumpUtil.convertFiles();
		String csvDir = LoadUtil.getConfig().required("brahms.csv_dir");
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(BrahmsDumpUtil.FILE_EXT_IMPORTABLE);
			}
		});
		if (files.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		int maxRecords = Integer.parseInt(System.getProperty("maxRecords", "0"));
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.setMaxRecords(maxRecords);
		BrahmsMultiMediaImporter mediaImporter = new BrahmsMultiMediaImporter(index);
		mediaImporter.setMaxRecords(maxRecords);
		for (File f : files) {
			specimenImporter.importCsv(f.getAbsolutePath());
			mediaImporter.importCsv(f.getAbsolutePath());
			if (backup) {
				f.renameTo(new File(f.getCanonicalPath() + BrahmsDumpUtil.FILE_EXT_IMPORTED));
			}
		}
	}
}
