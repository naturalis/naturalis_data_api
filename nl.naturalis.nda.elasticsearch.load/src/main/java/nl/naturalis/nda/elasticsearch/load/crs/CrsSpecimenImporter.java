package nl.naturalis.nda.elasticsearch.load.crs;

import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.DEFAULT_NDA_INDEX_NAME;
import static nl.naturalis.nda.elasticsearch.load.NDASchemaManager.LUCENE_TYPE_SPECIMEN;

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
import nl.naturalis.nda.elasticsearch.load.LoadUtil;

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

		IndexNative index = new IndexNative(LoadUtil.getDefaultClient(), DEFAULT_NDA_INDEX_NAME);

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

	private final DocumentBuilder builder;

	private final Index index;
	private final int bulkRequestSize;
	private final int maxRecords;
	private final boolean forceRestart;

	private int processed;
	private int bad;


	public CrsSpecimenImporter(Index index) throws Exception
	{
		this.index = index;
		String prop = System.getProperty("bulkRequestSize", "1000");
		bulkRequestSize = Integer.parseInt(prop);

		prop = System.getProperty("maxRecords", "0");
		maxRecords = Integer.parseInt(prop);

		prop = System.getProperty("forceRestart", "true");
		forceRestart = Boolean.parseBoolean(prop);

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

		int batch;

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
		if (xml == null) {
			// Missing local file
			return null;
		}

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
		int numRecords = records.getLength();
		logger.info("Number of records in XML output: " + numRecords);

		List<ESSpecimen> specimens = new ArrayList<ESSpecimen>(bulkRequestSize);
		List<String> ids = new ArrayList<String>(bulkRequestSize);
		for (int i = 0; i < numRecords; ++i) {
			++processed;
			try {
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
			}
			catch (Throwable t) {
				++bad;
				logger.error(t.getMessage(), t);
			}
			if (maxRecords > 0 && processed >= maxRecords) {
				break;
			}
			if (processed % 1000 == 0) {
				logger.info("Records processed: " + processed);
			}
		}
		if (!specimens.isEmpty()) {
			index.saveObjects(LUCENE_TYPE_SPECIMEN, specimens, ids);
		}
		if (maxRecords > 0 && processed >= maxRecords) {
			return null;
		}
		return DOMUtil.getDescendantValue(doc, "resumptionToken");
	}


	static String getXML(String resumptionToken)
	{
		String xml;
		ConfigObject config = LoadUtil.getConfig();
		if (config.getBoolean("crs.use_local")) {
			String path = getLocalFile(resumptionToken);
			File f = new File(path);
			if (!f.isFile()) {
				logger.warn(String.format("Missing local file: \"%s\"", path));
				xml = null;
			}
			else {
				logger.info("Loading file: " + path);
				xml = FileUtil.getContents(path);
			}
		}
		else {
			String url;
			if (resumptionToken == null) {
				url = config.get("crs.media.url.initial");
			}
			else {
				url = String.format(config.get("crs.media.url.resume"), resumptionToken);
			}
			logger.info("Calling service: " + url);
			// Avoid "Content is not allowed in prolog"
			xml = new SimpleHttpGet().setBaseUrl(url).execute().getResponse().trim();
			if (!xml.startsWith("<?xml")) {
				xml = xml.substring(xml.indexOf("<?xml"));
			}
		}
		return xml;
	}


	static String getLocalFile(String resumptionToken)
	{
		String testDir = LoadUtil.getConfig().required("crs.local_dir");
		return String.format("%s/specimens.%s.oai.xml", testDir, resumptionToken);
	}


	private static boolean isDeletedRecord(Element record)
	{
		if (!DOMUtil.getChild(record, "header").hasAttribute("status")) {
			return false;
		}
		return DOMUtil.getChild(record, "header").getAttribute("status").equals("deleted");
	}


	private static File getResumptionTokenFile()
	{
		return new File(System.getProperty("java.io.tmpdir") + "/crs-specimens.resumption-token");
	}

}
