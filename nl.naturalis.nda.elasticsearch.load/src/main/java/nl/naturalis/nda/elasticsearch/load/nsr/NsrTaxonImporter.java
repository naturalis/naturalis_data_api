package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_TAXON;

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
import nl.naturalis.nda.elasticsearch.load.InvalidDataException;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.MalformedDataException;
import nl.naturalis.nda.elasticsearch.load.SkippableDataException;

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
	private int totalIndexed = 0;
	private int totalSkipped = 0;
	private int totalRejected = 0;


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
		logger.info("Skipped records            (total) : " + String.format("%5d", totalSkipped));
		logger.info("Malformed/rejected records (total) : " + String.format("%5d", totalRejected));
		logger.info("Records indexed            (total) : " + String.format("%5d", totalIndexed));
		logger.info("Records processed          (total) : " + String.format("%5d", totalProcessed));
	}


	public void importXmlFile(Document doc)
	{
		int processed = 0;
		int indexed = 0;
		int skipped = 0;
		int rejected = 0;

		Element taxaElement = DOMUtil.getChild(doc.getDocumentElement());
		List<Element> taxonElements = DOMUtil.getChildren(taxaElement);

		List<ESTaxon> taxa = new ArrayList<ESTaxon>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);

		ESTaxon taxon = null;
		for (Element taxonElement : taxonElements) {
			++totalProcessed;
			if (++processed % 5000 == 0) {
				logger.info("Records processed: " + processed);
			}
			try {
				taxon = NsrTaxonTransfer.transfer(taxonElement);
				if (taxon != null) {
					taxa.add(taxon);
					ids.add(ID_PREFIX + taxon.getSourceSystemId());
					if (taxa.size() >= bulkRequestSize) {
						try {
							index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
							indexed += taxa.size();
							totalIndexed += taxa.size();
						}
						finally {
							taxa.clear();
							ids.clear();
						}
					}
				}
			}
			catch (SkippableDataException e) {
				++skipped;
				++totalSkipped;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Skipping record %s (\"%s\"): %s", (processed + 1), name, e.getMessage());
				logger.debug(msg);
			}
			catch (MalformedDataException | InvalidDataException e) {
				++rejected;
				++totalRejected;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Invalid or malformed data in record %s (\"%s\"): %s", (processed + 1), name, e.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", e);
			}
			catch (Throwable t) {
				++rejected;
				++totalRejected;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Error while processing record %s (\"%s\"): %s", (processed + 1), name, t.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", t);
			}
		}
		if (!taxa.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_TAXON, taxa, ids);
			indexed += taxa.size();
			totalIndexed += taxa.size();
		}

		logger.info("Records indexed            : " + String.format("%5d", indexed));
		logger.info("Records skipped            : " + String.format("%5d", skipped));
		logger.info("Rejected/malformed records : " + String.format("%5d", rejected));
		logger.info("Records processed          : " + String.format("%5d", processed));
	}

}
