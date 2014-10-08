package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_TAXON;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class NsrImportAll {

	public static void main(String[] args) throws Exception
	{
		
		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");
		
		IndexNative index = new IndexNative(DEFAULT_NDA_INDEX_NAME);
		
		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType(LUCENE_TYPE_TAXON);
			index.deleteType(LUCENE_TYPE_MULTIMEDIA_OBJECT);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE_TAXON, mapping);
			mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_TAXON)) {
				index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.NSR.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
				index.addType(LUCENE_TYPE_TAXON, mapping);				
			}
			if (index.typeExists(LUCENE_TYPE_MULTIMEDIA_OBJECT)) {
				index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.NSR.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
				index.addType(LUCENE_TYPE_TAXON, mapping);				
			}
		}
		
		try {
			NsrImportAll importer = new NsrImportAll(index);
			importer.importXmlFiles();
		}
		finally {
			index.getClient().close();
		}
		
	}

	static final Logger logger = LoggerFactory.getLogger(NsrImportAll.class);

	private final IndexNative index;
	private final boolean rename;


	public NsrImportAll(IndexNative index)
	{
		this.index = index;
		String prop = System.getProperty("rename", "false");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importXmlFiles() throws Exception
	{
		String xmlDir = System.getProperty("xmlDir");
		if (xmlDir == null) {
			throw new Exception("Missing -DxmlDir argument");
		}
		File file = new File(xmlDir);
		if (!file.isDirectory()) {
			throw new Exception(String.format("No such directory: \"%s\"", xmlDir));
		}
		File[] xmlFiles = file.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				return name.toLowerCase().endsWith(".xml");
			}
		});
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}

		NsrTaxonImporter taxonImporter = new NsrTaxonImporter(index);
		NsrMultiMediaImporter multimediaImporter = new NsrMultiMediaImporter(index);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		for (File xmlFile : xmlFiles) {
			logger.info("Processing file " + xmlFile.getCanonicalPath());
			Document document = builder.parse(xmlFile);
			taxonImporter.importXmlFile(document);
			multimediaImporter.importXmlFile(document);
			if (rename) {
				xmlFile.renameTo(new File(xmlFile.getCanonicalPath() + ".bak"));
			}
		}

	}

}
