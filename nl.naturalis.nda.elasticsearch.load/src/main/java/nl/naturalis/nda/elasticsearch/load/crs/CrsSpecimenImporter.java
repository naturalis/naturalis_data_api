package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.Index;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.*;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
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
public class CrsSpecimenImporter {

	public static void main(String[] args) throws Exception
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		IndexNative index = new IndexNative(DEFAULT_NDA_INDEX_NAME);

		String rebuild = System.getProperty("rebuild", "false");
		if (rebuild.equalsIgnoreCase("true") || rebuild.equals("1")) {
			index.deleteType(LUCENE_TYPE_SPECIMEN);
			String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
			index.addType(LUCENE_TYPE_SPECIMEN, mapping);
		}
		else {
			if (index.typeExists(LUCENE_TYPE_SPECIMEN)) {
				index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.CRS.getCode());
			}
			else {
				String mapping = StringUtil.getResourceAsString("/es-mappings/Specimen.json");
				index.addType(LUCENE_TYPE_SPECIMEN, mapping);
			}
		}

		try {
			CrsSpecimenImporter importer = new CrsSpecimenImporter(index);
			importer.importSpecimens();
		}
		finally {
			index.getClient().close();
		}
		logger.info("Ready");

	}

	private static final Logger logger = LoggerFactory.getLogger(CrsSpecimenImporter.class);
	private static final String ID_PREFIX = "CRS-";

	private final ConfigObject config;
	private final DocumentBuilder builder;

	private int processed;
	private int bad;

	private final Index index;
	private final int bulkRequestSize;
	private final int maxNumBatches;
	private final boolean forceRestart;


	public CrsSpecimenImporter(Index index) throws Exception
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);

		prop = System.getProperty("maxNumBatches", "0");
		maxNumBatches = Integer.parseInt(prop);

		prop = System.getProperty("forceRestart", "true");
		forceRestart = Boolean.parseBoolean(prop);

		prop = System.getProperty("configFile");
		if (prop == null) {
			throw new Exception("Missing property -DconfigFile");
		}
		File f = new File(prop);
		if (!f.isFile()) {
			throw new Exception(String.format("Configuration file not found : \"%s\"", prop));
		}
		config = new ConfigObject(f);

		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	public void importSpecimens()
	{

		int batch = 0;

		try {

			String resToken;
			File resTokenFile = getResumptionTokenFile();
			logger.info(String.format("Looking for resumption token file: %s", resTokenFile.getCanonicalPath()));
			if (!resTokenFile.exists()) {
				logger.info("Resumption token file not found. Will start from scratch");
				resToken = null;
				batch = 0;
			}
			else {
				if (forceRestart) {
					resTokenFile.delete();
					logger.info("Resumption token file found but ignored and deleted (forceRestart=true). Will start from scratch");
					resToken = null;
					batch = 0;
				}
				else {
					String[] elements = FileUtil.getContents(resTokenFile).split(",");
					batch = Integer.parseInt(elements[0]);
					resToken = elements[1];
					logger.info(String.format("Will resume with resumption token %s (batch %s)", resToken, batch));
				}
			}

			processed = 0;
			bad = 0;

			do {
				logger.info("Processing batch " + batch);
				resToken = processXML(batch++, resToken);
				if (batch > maxNumBatches) {
					break;
				}
			} while (resToken != null);

			logger.info("Deleting resumption token file");
			if (resTokenFile.exists()) {
				resTokenFile.delete();
			}

			logger.info("Records processed: " + processed);
			logger.info("Bad records: " + bad);
			logger.info(getClass().getSimpleName() + " finished successfully");

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}

	}


	private String processXML(int batch, String resumptionToken)
	{

		if (resumptionToken != null) {
			logger.info("Saving resumption token");
			String contents = String.valueOf(batch) + "," + resumptionToken;
			FileUtil.setContents(getResumptionTokenFile(), contents);
		}

		String xml = getXML(resumptionToken);
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
		int numRecords = records.getLength();
		logger.info("Number of records in XML output: " + numRecords);

		List<ESSpecimen> specimens = new ArrayList<ESSpecimen>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);
		for (int i = 0; i < numRecords; ++i) {
			Element record = (Element) records.item(i);
			String id = ID_PREFIX + DOMUtil.getDescendantValue(record, "identifier");
			if (isDeletedRecord(record)) {
				index.deleteDocument(LUCENE_TYPE_SPECIMEN, id);
			}
			else {
				specimens.add(CrsSpecimenTransfer.transfer(record));
				ids.add(id);
				if (specimens.size() >= bulkRequestSize) {
					index.saveObjects(LUCENE_TYPE_SPECIMEN, specimens, ids);
					specimens.clear();
					ids.clear();
				}
			}
			if (++processed % 1000 == 0) {
				logger.info("Records processed: " + processed);
			}
		}
		if (!specimens.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_SPECIMEN, specimens, ids);
		}
		return getResumptionToken(doc);
	}


	private String getXML(String resumptionToken)
	{
		if (config.getBoolean("test")) {
			String key = resumptionToken == null ? "test.specimens.url.initial" : "test.specimens.url.resume";
			String val = config.getString(key);
			logger.info("Loading file: " + val);
			return FileUtil.getContents(val);
		}
		String url;
		if (resumptionToken == null) {
			url = config.getString("specimens.url.initial");
		}
		else {
			url = String.format(config.getString("specimens.url.resume"), resumptionToken);
		}
		logger.info("Calling service: " + url);
		// Avoid "Content is not allowed in prolog"
		String response = new SimpleHttpGet().setBaseUrl(url).execute().getResponse().trim();
		if (!response.startsWith("<?xml")) {
			response = response.substring(response.indexOf("<?xml"));
		}
		return response;
	}


	private static boolean isDeletedRecord(Element record)
	{
		if (!DOMUtil.getChild(record, "header").hasAttribute("status")) {
			return false;
		}
		return DOMUtil.getChild(record, "header").getAttribute("status").equals("deleted");
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
		return new File(System.getProperty("java.io.tmpdir") + "/resumption-token");
	}

}
