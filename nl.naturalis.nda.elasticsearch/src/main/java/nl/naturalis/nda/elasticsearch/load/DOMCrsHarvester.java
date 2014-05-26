package nl.naturalis.nda.elasticsearch.load;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.bioportal.oaipmh.CRSBioportalInterface;
import nl.naturalis.nda.domain.Specimen;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
//import org.domainobject.util.debug.BeanPrinter;
import org.domainobject.util.http.SimpleHttpGet;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DOMCrsHarvester {

	public static void main(String[] args)
	{
		DOMCrsHarvester harvester = new DOMCrsHarvester();
		harvester.harvest();
	}

	private static final Logger logger = LoggerFactory.getLogger(DOMCrsHarvester.class);

	private static final String RES_TOKEN_FILE = "/.resumption-token";
	private static final String RES_TOKEN_DELIM = ",";

	private final ConfigObject config;
	private final DocumentBuilder builder;
	//private final BeanPrinter beanPrinter;
	private final CRSTransfer crsTransfer;
	private final ObjectMapper objectMapper;
	private final IndexRequestBuilder indexRequestBuilder;

	private int batch;
	private int recordsProcessed;
	private int badRecords;


	public DOMCrsHarvester()
	{
		URL url = CRSBioportalInterface.class.getResource("/config/import-crs.properties");
		config = new ConfigObject(url);
		//beanPrinter = new BeanPrinter("C:/tmp/BeanPrinter.txt");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
		crsTransfer = new CRSTransfer();
		objectMapper = new ObjectMapper();
		Client esClient = nodeBuilder().node().client();
		indexRequestBuilder = esClient.prepareIndex(SchemaCreator.NDA_INDEX_NAME, "specimen");
	}


	public void testEL() throws JsonProcessingException
	{
		Specimen specimen = new Specimen();
		specimen.setAltitude("altitude");
		specimen.setCountry("country");
		specimen.setDepth("depth");
		specimen.setAccessionSpecimenNumbers("accessionSpecimenNumbers");
		specimen.setAltitudeUnit("altitudeUnit");

		String json = objectMapper.writeValueAsString(specimen);

		IndexResponse response = indexRequestBuilder.setSource(json).execute().actionGet();
		System.out.println("_index: " + response.getIndex());
		System.out.println("_id: " + response.getId());
		System.out.println("_type: " + response.getType());
		System.out.println("Created: " + response.isCreated());

	}


	public void harvest()
	{

		try {

			String resToken;
			File resTokenFile = getResumptionTokenFile();
			if (!resTokenFile.exists()) {
				logger.info(String.format("Did not find resumption token file: %s", resTokenFile.getCanonicalPath()));
				logger.info("Will start from scratch (batch 0)");
				resToken = null;
				batch = 0;
			}
			else {
				String[] elements = FileUtil.getContents(resTokenFile).split(RES_TOKEN_DELIM);
				resToken = elements[0];
				batch = Integer.parseInt(elements[1]);
				logger.info(String.format("Found resumption token file: %s", resTokenFile.getCanonicalPath()));
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
		catch (SAXException | IOException e) {
			throw ExceptionUtil.smash(e);
		}
		doc.normalize();
		NodeList records = doc.getElementsByTagName("record");
		int batchSize = records.getLength();
		logger.info("Records in batch: " + batchSize);
		for (int i = 0; i < batchSize; ++i) {
			Element record = (Element) records.item(i);
			Element header = getChild(record, "header");
			String id = getChild(header, "identifier").getTextContent();
			if (header.hasAttribute("status") && header.getAttribute("status").equals("deleted")) {
				deleteRecord(id);
			}
			else {
				Specimen specimen = crsTransfer.createSpecimen(record);
				saveSpecimen(specimen);
			}
			if (++recordsProcessed % 1000 == 0) {
				logger.info("Records processed: " + recordsProcessed);
			}
		}
		return getResumptionToken(doc);
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
		FileUtil.putContents(resTokenFile, String.valueOf(batch) + "," + resToken);
	}


	private void saveSpecimen(Specimen specimen)
	{
		final String json;
		try {
			json = objectMapper.writeValueAsString(specimen);
		}
		catch (JsonProcessingException e) {
			throw new HarvestException(e);
		}
		String docId = specimen.getSourceSystemName() + "-" + specimen.getSpecimenId();
		indexRequestBuilder.setId(docId);
		IndexResponse response = indexRequestBuilder.setSource(json).execute().actionGet();
		if (!response.isCreated()) {
			String fmt = "Failed to create specimen (source system: %s; source system id: %s)";
			String err = String.format(fmt, specimen.getSourceSystemName(), specimen.getSourceSystemId());
			logger.error(err);
		}
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
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponse();
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
