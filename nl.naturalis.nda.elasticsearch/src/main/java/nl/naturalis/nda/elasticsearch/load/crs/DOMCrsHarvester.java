package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.HarvestException;
import nl.naturalis.nda.elasticsearch.load.NDASchemaManager;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.domainobject.util.debug.BeanPrinter;
import org.domainobject.util.http.SimpleHttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * ETL class using CRS's OAIPMH service to extract the data, w3c DOM to parse
 * the data, and ElasticSearch's native client to save the data.
 * 
 * @author ayco_holleman
 * 
 */
public class DOMCrsHarvester {

	private static final String TYPE_SPECIMEN = "specimen";


	public static void main(String[] args)
	{
		DOMCrsHarvester harvester = new DOMCrsHarvester();
		harvester.harvest();
	}

	private static final Logger logger = LoggerFactory.getLogger(DOMCrsHarvester.class);

	private static final String RES_TOKEN_FILE = "/resumption-token";
	private static final String RES_TOKEN_DELIM = ",";

	private final ConfigObject config;
	private final DocumentBuilder builder;
	private final CRSTransfer crsTransfer;

	private int batch;
	private int recordsProcessed;
	private int badRecords;

	private Index index;


	public DOMCrsHarvester()
	{
		URL url = DOMCrsHarvester.class.getResource("/config/crs/import-crs.properties");
		config = new ConfigObject(url);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
		crsTransfer = new CRSTransfer();
	}


	public void harvest()
	{
		try {

			index = new IndexNative(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);
			//index = new IndexREST(NDASchemaManager.DEFAULT_NDA_INDEX_NAME);

			String resToken;
			File resTokenFile = getResumptionTokenFile();
			logger.info(String.format("Looking for resumption token file: %s", resTokenFile.getCanonicalPath()));
			if (!resTokenFile.exists()) {
				logger.info("Resumption token file not found. Will start from scratch");
				resToken = null;
				batch = 0;
			}
			else {
				String[] elements = FileUtil.getContents(resTokenFile).split(RES_TOKEN_DELIM);
				batch = Integer.parseInt(elements[0]);
				resToken = elements[1];
				logger.info(String.format("Will resume with resumption token %s (batch %s)", resToken, batch));
			}

			recordsProcessed = 0;
			badRecords = 0;

			do {
				logger.info("Processing batch " + batch);
				resToken = processBatch(resToken);
				++batch;
			} while (resToken != null);

			logger.info("Deleting resumption token file");
			if (resTokenFile.exists()) {
				resTokenFile.delete();
			}

			logger.info("Records processed: " + recordsProcessed);
			logger.info("Bad records: " + badRecords);
			logger.info(getClass().getSimpleName() + " finished successfully");

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}
		finally {
			if (index != null && index instanceof IndexNative) {
				((IndexNative) index).getClient().close();
			}
		}

	}


	private String processBatch(String resToken)
	{
		if (resToken != null) {
			logger.info("Saving resumption token");
			saveResumptionToken(resToken);
		}
		logger.info("Calling CRS OAI service");
		String xml = getXML(resToken);
		Document doc;
		logger.info("Parsing XML");
		try {
			doc = builder.parse(StringUtil.asInputStream(xml));
		}
		catch (SAXException e) {
			throw ExceptionUtil.smash(e);
		}
		catch (IOException e) {
			throw ExceptionUtil.smash(e);
		}
		doc.normalize();
		NodeList records = doc.getElementsByTagName("record");
		int batchSize = records.getLength();
		logger.info("Records in batch: " + batchSize);
		for (int i = 0; i < batchSize; ++i) {
			Element record = (Element) records.item(i);
			saveSpecimen(record, i);
			if (++recordsProcessed % 1000 == 0) {
				logger.info("Records processed: " + recordsProcessed);
			}
		}
		return getResumptionToken(doc);
	}


	private void saveSpecimen(Element record, int recNo)
	{
		Specimen specimen = null;
		try {
			Element header = getChild(record, "header");
			String id = getChild(header, "identifier").getTextContent();
			if (header.hasAttribute("status") && header.getAttribute("status").equals("deleted")) {
				deleteRecord(id);
			}
			else {
				specimen = crsTransfer.createSpecimen(record);
				specimen.setSpecimenId(specimen.getSpecimenId().trim());
				index.saveObject(TYPE_SPECIMEN, specimen, specimen.getSpecimenId());
			}
		}
		catch (Throwable t) {
			++badRecords;
			String fmt = "Error while processing record %s, specimen \"%s\": %s";
			String msg = String.format(fmt, recNo, specimen.getSpecimenId(), t.getMessage());
			logger.error(msg);
			logger.error("Stack trace for error: ", t);
		}
	}


	private void deleteRecord(String id)
	{
	}


	private void saveResumptionToken(String resToken)
	{
		File resTokenFile = getResumptionTokenFile();
		if (resTokenFile.exists()) {
			resTokenFile.delete();
		}
		FileUtil.setContents(resTokenFile, String.valueOf(batch) + "," + resToken);
	}


	private static String getResumptionToken(Document doc)
	{
		NodeList nl = doc.getElementsByTagName("resumptionToken");
		if (nl.getLength() == 0) {
			return null;
		}
		return nl.item(0).getTextContent();
	}


	private static File getResumptionTokenFile()
	{
		return new File(System.getProperty("java.io.tmpdir") + RES_TOKEN_FILE);
	}


	private static Element getChild(Element e, String childName)
	{
		NodeList nl = e.getElementsByTagName(childName);
		if (nl.getLength() != 0) {
			return (Element) nl.item(0);
		}
		throw new HarvestException("No such child element: " + childName);
	}


	private String getXML(String resToken)
	{
		URL url = getServiceUrl(resToken);
		if (config.getBoolean("isTest")) {
			return FileUtil.getContents(url);
		}
		return new SimpleHttpGet().setBaseUrl(url.toExternalForm()).execute().getResponse();
	}


	private URL getServiceUrl(String resToken)
	{
		if (config.getBoolean("isTest")) {
			String key = resToken == null ? "service.url.initial.test" : "service.url.resume.test";
			String val = config.getString(key);
			return getClass().getResource(val);
		}
		String key = resToken == null ? "service.url.initial" : "service.url.resume";
		String val = config.getString(key);
		try {
			return new URL(val);
		}
		catch (MalformedURLException e) {
			throw ExceptionUtil.smash(e);
		}
	}

}
