package nl.naturalis.nda.elasticsearch.load.brahms;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrahmsImportAll {

	public static void main(String[] args) throws Exception
	{
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		String rebuild = System.getProperty("rebuild", "false");
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType("Specimen");
			index.deleteType("MultiMediaObject");
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType("Specimen", mapping);
			mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType("MultiMediaObject", mapping);
		}
		else {
			index.deleteWhere("Specimen", "sourceSystem.code", SourceSystem.BRAHMS.getCode());
			index.deleteWhere("MultiMediaObject", "sourceSystem.code", SourceSystem.BRAHMS.getCode());
		}
		Thread.sleep(2000);
		try {
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importCsvFiles();
		}
		finally {
			index.getClient().close();
		}
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
