package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_TAXON;

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
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NsrTaxonImporter {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), DEFAULT_NDA_INDEX_NAME);

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_TAXON);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
			index.addType(LUCENE_TYPE_TAXON, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_TAXON)) {
				index.deleteWhere(LUCENE_TYPE_TAXON, "sourceSystem.code", SourceSystem.NSR.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Taxon.json");
				index.addType(LUCENE_TYPE_TAXON, mapping);
			}
		}
		try {
			NsrTaxonImporter importer = new NsrTaxonImporter(index);
			importer.importXmlFiles();
		}
		finally {
			index.getClient().close();
		}
		logger.info("NsrTaxonImporter finished");
	}

	private static final Logger logger = LoggerFactory.getLogger(NsrTaxonImporter.class);
	private static final String ID_PREFIX = "NSR-";

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
		prop = System.getProperty("rename", "0");
		rename = prop.equals("1") || prop.equalsIgnoreCase("true");
	}


	public void importXmlFiles() throws Exception
	{
		String xmlDir = LoadUtil.getConfig().required("nsr.xml_dir");
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
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		for (File xmlFile : xmlFiles) {
			logger.info("Processing file " + xmlFile.getCanonicalPath());
			Document document = builder.parse(xmlFile);
			importXmlFile(document);
			if (rename) {
				xmlFile.renameTo(new File(xmlFile.getCanonicalPath() + ".bak"));
			}
		}
		logger.info("Total number of records processed: " + totalProcessed);
		logger.info("Total number of bad records: " + totalBad);
		logger.info("Ready");
	}


	public void importXmlFile(Document doc)
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
				taxa.add(taxon);
				ids.add(ID_PREFIX + taxon.getSourceSystemId());
				if (taxa.size() >= bulkRequestSize) {
					index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
					taxa.clear();
					ids.clear();
				}
			}
			catch (Throwable t) {
				++bad;
				++totalBad;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Error in record %s (\"%s\"): %s", (processed + 1), name, t.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", t);
			}
		}
		if (!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
		}

		logger.info("Records processed: " + processed);
		logger.info("Bad records: " + bad);
	}

}
