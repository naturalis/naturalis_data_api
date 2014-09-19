package nl.naturalis.nda.elasticsearch.load.nsr;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESTaxon;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NsrTaxonImporter {

	public static void main(String[] args) throws Exception
	{
		String rebuild = System.getProperty("rebuild", "false");
		IndexNative index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
		if (rebuild != null && (rebuild.equalsIgnoreCase("true") || rebuild.equals("1"))) {
			index.deleteType(LUCENE_TYPE);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE, mapping);
		}
		else {
			index.deleteWhere(LUCENE_TYPE, "sourceSystem.code", SourceSystem.NSR.getCode());
		}
		Thread.sleep(2000);
		try {
			NsrTaxonImporter importer = new NsrTaxonImporter(index);
			importer.importXmlFiles();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(NsrTaxonImporter.class);
	private static final String ID_PREFIX = "NSR-";
	private static final String LUCENE_TYPE = "Taxon";

	private final Index index;

	private final int bulkRequestSize;
	private final boolean rename;
	
	private int totalProcessed = 0;
	private int totalBad = 0;


	public NsrTaxonImporter(Index index)
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);
		prop = System.getProperty("rename", "1");
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
				return name.endsWith(".xml");
			}
		});
		if (xmlFiles.length == 0) {
			logger.info("No XML files to process");
			return;
		}
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		for (File xmlFile : xmlFiles) {
			logger.info("Processing file " + xmlFile.getCanonicalPath());
			Document document = builder.parse(xmlFile);
			importXmlFile(document);
			if(rename) {
				xmlFile.renameTo(new File(xmlFile.getCanonicalPath() + ".bak"));
			}
		}
		logger.info("Total number of records processed: " + totalProcessed);
		logger.info("Total number of bad records: " + totalBad);
		logger.info("Ready");
	}


	private void importXmlFile(Document doc)
	{
		int processed = 0;
		int bad = 0;
		
		Element taxaElement = DOMUtil.getChild(doc.getDocumentElement());
		List<Element> taxonElements = DOMUtil.getChildren(taxaElement);

		List<ESTaxon> taxa = new ArrayList<ESTaxon>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);

		ESTaxon taxon = null;
		for (Element taxonElement : taxonElements) {
			++totalProcessed;
			if (++processed % 1000 == 0) {
				logger.info("Records processed: " + processed);
			}
			try {
				taxon = NsrTaxonTransfer.transfer(taxonElement);
			}
			catch (Throwable t) {
				++bad;
				++totalBad;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Error in record %s (\"%s\"): %s", (processed + 1), name, t.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", t);
			}
			taxa.add(taxon);
			ids.add(ID_PREFIX + taxon.getSourceSystemId());
			if (taxa.size() >= bulkRequestSize) {
				index.saveObjects(LUCENE_TYPE, taxa, ids);
				taxa.clear();
				ids.clear();
			}
		}
		if (!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE, taxa, ids);
		}
		
		logger.info("Records processed: " + processed);
		logger.info("Bad records: " + bad);
	}

}
