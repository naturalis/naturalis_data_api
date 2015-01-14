package nl.naturalis.nda.elasticsearch.load.nsr;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NsrMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(LoadUtil.getESClient(), LoadUtil.getConfig().required("elasticsearch.index.name"));

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_MULTIMEDIA_OBJECT);
			String mapping = StringUtil.getResourceAsString("/es-mappings/MultiMediaObject.json");
			index.addType(LUCENE_TYPE_MULTIMEDIA_OBJECT, mapping);
		}
		else {
			index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.NSR.getCode());
		}
		try {
			NsrMultiMediaImporter importer = new NsrMultiMediaImporter(index);
			importer.importXmlFiles();
		}
		finally {
			index.getClient().close();
		}
		logger.info("NsrMultiMediaImporter finished");
	}

	private static final Logger logger = LoggerFactory.getLogger(NsrMultiMediaImporter.class);

	private final Index index;

	private final int bulkRequestSize;
	private final boolean rename;

	private int totalNumTaxa = 0;
	private int totalNumImages = 0;
	private int totalNumBadTaxa = 0;


	public NsrMultiMediaImporter(Index index)
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
		logger.info("Total number of taxa processed: " + totalNumTaxa);
		logger.info("Total number of bad taxa: " + totalNumBadTaxa);
		logger.info("Total number of imported images: " + totalNumImages);
		logger.info("Ready");
	}


	public void importXmlFile(Document doc)
	{
		int numTaxa = 0;
		int numImages = 0;
		int numBadTaxa = 0;

		Element taxaElement = DOMUtil.getChild(doc.getDocumentElement());
		List<Element> taxonElements = DOMUtil.getChildren(taxaElement);

		List<ESMultiMediaObject> batch = new ArrayList<ESMultiMediaObject>(bulkRequestSize);

		for (Element taxonElement : taxonElements) {
			++totalNumTaxa;
			if (++numTaxa % 1000 == 0) {
				logger.info("Records processed: " + numTaxa);
			}
			try {
				List<ESMultiMediaObject> mmos = NsrMultiMediaTransfer.getImages(taxonElement);
				if (mmos != null) {
					batch.addAll(NsrMultiMediaTransfer.getImages(taxonElement));
				}
			}
			catch (Throwable t) {
				++numBadTaxa;
				++totalNumBadTaxa;
				String name = DOMUtil.getValue(taxonElement, "name");
				String msg = String.format("Error in taxon \"%s\": %s", name, t.getMessage());
				logger.error(msg);
				logger.debug("Stack trace:", t);
			}
			if (batch.size() >= bulkRequestSize) {
				index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, batch);
				numImages += batch.size();
				totalNumImages += batch.size();
				batch.clear();
			}
		}
		if (!batch.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_MULTIMEDIA_OBJECT, batch);
			numImages += batch.size();
			totalNumImages += batch.size();
		}

		logger.info("Number of taxa processed: " + numTaxa);
		logger.info("Number of bad taxa: " + numBadTaxa);
		logger.info("Number of imported images: " + numImages);
	}

}
