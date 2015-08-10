package nl.naturalis.nda.elasticsearch.load.crs;

import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.domainobject.util.http.SimpleHttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CrsPing {

	public static void main(String[] args)
	{

		logger.info("Preparing tests ...");
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		DocumentBuilder builder;
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			logger.error("Preparation failed: " + e.getMessage());
			return;
		}

		int numTests = args.length == 0 ? 10 : Integer.parseInt(args[0]);

		logger.info("Testing specimens ...");
		boolean specimensOk = ping(builder, numTests, SPECIMEN_URL);
		logger.info("Testing multimedia ...");
		boolean multimediaOk = ping(builder, numTests, MEDIA_URL);

		if (specimensOk && multimediaOk) {
			logger.info("All tests successful!");
		}
	}


	private static boolean ping(DocumentBuilder builder, int numTests, String url)
	{
		try {
			for (int i = 0; i < numTests; ++i) {
				logger.info("Calling service: " + url);
				byte[] response = new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
				String xml = new String(response, Charset.forName("UTF-8"));
				if (!xml.startsWith("<?xml")) {
					if (xml.indexOf("<?xml") == -1) {
						logger.error(xml);
						throw new Exception("Unexpected response: " + xml);
					}
					xml = xml.substring(xml.indexOf("<?xml"));
				}
				Document doc = builder.parse(StringUtil.toInputStream(xml));
				if (!doc.getDocumentElement().getTagName().equals("OAI-PMH")) {
					logger.error(xml);
					throw new Exception("XML response not according to OAI-PMH schema");
				}
				Element e = DOMUtil.getDescendant(doc.getDocumentElement(), "error");
				if (e != null) {
					logger.error(xml);
					String msg = String.format("OAI Error (code=\"%s\"): \"%s\"", e.getAttribute("code"), e.getTextContent());
					throw new Exception(msg);
				}
			}
			logger.info("Test successful");
			return true;
		}
		catch (Throwable t) {
			logger.error("Test failed: " + t.getMessage());
			return false;
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsPing.class);
	public static final String SPECIMEN_URL = "http://crspl.naturalis.nl/Atlantispubliek/oai.axd?verb=ListRecords&metadataprefix=oai_crs_object";
	public static final String MEDIA_URL = "http://crspl.naturalis.nl/atlantispubliek/oai.axd?verb=ListRecords&metadataprefix=oai_crs";

}
