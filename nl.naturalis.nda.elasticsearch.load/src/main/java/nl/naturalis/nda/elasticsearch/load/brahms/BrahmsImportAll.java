package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.io.FilenameFilter;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.ThematicSearchConfig;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsImportAll {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));

		// Check thematic search is configured properly
		ThematicSearchConfig.getInstance();				

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_SPECIMEN);
			index.deleteType(LUCENE_TYPE_MULTIMEDIA_OBJECT);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType(LUCENE_TYPE_SPECIMEN, mapping);
			mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_SPECIMEN)) {
				index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
				index.addType(LUCENE_TYPE_SPECIMEN, mapping);
			}
			if (index.typeExists(LUCENE_TYPE_MULTIMEDIA_OBJECT)) {
				index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
				index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
			}
		}

		try {
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importCsvFiles();
		}
		finally {
			index.getClient().close();
		}
		logger.info("Ready");
	}

	private static final Logger logger = LoggerFactory.getLogger(BrahmsImportAll.class);

	private final IndexNative index;
	private final boolean backup;


	public BrahmsImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty("nl.naturalis.nda.elasticsearch.load.brahms.backup", "true");
		backup = prop.equals("1") || prop.equalsIgnoreCase("true");
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
				return name.toLowerCase().endsWith(BrahmsDumpUtil.FILE_EXT_PROCESSABLE);
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
				f.renameTo(new File(f.getCanonicalPath() + BrahmsDumpUtil.FILE_EXT_PROCESSED));
			}
		}
	}
}
