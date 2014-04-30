package nl.naturalis.nda.elasticsearch.load;

import java.io.File;
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
import org.domainobject.util.debug.BeanPrinter;
import org.domainobject.util.http.SimpleHttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DOMCrsHarvester {

	public static void main(String[] args)
	{
		DOMCrsHarvester harvester = new DOMCrsHarvester();
		harvester.harvest();
	}

	private static final Logger logger = LoggerFactory.getLogger(DOMCrsHarvester.class);

	private static final String CONFIG_FILE = "/config/import-crs.properties";
	private static final String RES_TOKEN_FILE = "/.resumption-token";
	private static final String RES_TOKEN_DELIM = ",";

	private final ConfigObject config;
	private final DocumentBuilder builder;
	private final BeanPrinter beanPrinter;

	private int batch;


	public DOMCrsHarvester()
	{
		URL url = CRSBioportalInterface.class.getResource(CONFIG_FILE);
		config = new ConfigObject(url);
		beanPrinter = new BeanPrinter("C:/tmp/BeanPrinter.txt");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(true);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	public void harvest()
	{

		try {

			String resToken;
			File resTokenFile = getResumptionTokenFile();
			if (!resTokenFile.exists()) {
				logger.info(String.format("Did not find resumption token file: %s", resTokenFile.getCanonicalPath()));
				logger.info("Will start from scratch (batch 0)");
				batch = 0;
				resToken = null;
			}
			else {
				String[] elements = FileUtil.getContents(resTokenFile).split(RES_TOKEN_DELIM);
				batch = Integer.parseInt(elements[1]);
				resToken = elements[0];
				logger.info(String.format("Found resumption token file: %s", resTokenFile.getCanonicalPath()));
				logger.info(String.format("Will resume with resumption token %s (batch %s)", resToken, batch));
			}

			do {
				logger.info("Processing batch " + batch);
				resToken = processBatch(resToken);
				++batch;
				break;
			} while (resToken != null);

			logger.info("Deleting resumption token file");
			if (resTokenFile.exists()) {
				resTokenFile.delete();
			}

			logger.info(getClass().getSimpleName() + " finished successfully");

		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " did not complete successfully", t);
		}

	}


	private String processBatch(String resToken)
	{
		logger.info("Calling CRS OAI service");
		String xml = getXML(resToken);
		Document doc;
		try {
			logger.info("Parsing XML");
			doc = builder.parse(StringUtil.asInputStream(xml));
			//doc.normalize();
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
					Specimen specimen = CRSTransfer.createSpecimen(record);
					beanPrinter.dump(specimen);
				}
			}
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
		return null;
	}


	private void deleteRecord(String id)
	{
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
