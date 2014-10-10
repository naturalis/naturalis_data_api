package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_SPECIMEN;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsImportAll {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(DEFAULT_NDA_INDEX_NAME);
		
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

	static final Logger logger = LoggerFactory.getLogger(BrahmsImportAll.class);

	private final IndexNative index;
	private final boolean rename;


	public BrahmsImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty("rename", "false");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importCsvFiles() throws Exception
	{
		String csvDir = System.getProperty("csvDir");
		if (csvDir == null) {
			throw new Exception("Missing -DcsvDir argument");
		}
		File file = new File(csvDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", csvDir));
		}
		File[] files = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".csv");
			}
		});
		if (files.length == 0) {
			logger.info("No CSV files to process");
			return;
		}

		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		BrahmsMultiMediaImporter mediaImporter = new BrahmsMultiMediaImporter(index);

		for (File f : files) {
			specimenImporter.importCsv(f.getCanonicalPath());
			mediaImporter.importCsv(f.getCanonicalPath());
			if (rename) {
				String now = new SimpleDateFormat("yyyyMMdd").format(new Date());
				f.renameTo(new File(f.getCanonicalPath() + "." + now + ".bak"));
			}
		}
	}
}
