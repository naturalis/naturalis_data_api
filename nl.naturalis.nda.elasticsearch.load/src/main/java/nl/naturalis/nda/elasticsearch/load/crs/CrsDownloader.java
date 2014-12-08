package nl.naturalis.nda.elasticsearch.load.crs;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Harvests the CRS OAI service and saves its output to local files. Since the
 * OAI service is very slow, it pays to first store its output into files, and
 * then process those, if we often need to do a full harvest. You can also
 * instruct the {@link CrsSpecimenImporter} and the
 * {@link CrsMultiMediaImporter} to save local copies of the XML output from the
 * OAI service by setting the "crs.save_local" property in the configuration
 * file to "true".
 * 
 * @author ayco_holleman
 * 
 */
public class CrsDownloader {

	public static void main(String[] args)
	{

		logger.info("-----------------------------------------------------------------");
		logger.info("-----------------------------------------------------------------");

		try {
			CrsDownloader downloader = new CrsDownloader();

			if (args.length == 0) {
				downloader.download(Type.SPECIMEN, null);
				downloader.download(Type.MULTIMEDIA, null);
			}

			else if (args.length == 1) {
				if (args[0].toLowerCase().equals("specimens")) {
					downloader.download(Type.SPECIMEN, null);
				}
				else if (args[0].toLowerCase().equals("multimedia")) {
					downloader.download(Type.MULTIMEDIA, null);
				}
				else {
					logger.error(USAGE);
				}
			}

			else if (args.length == 2) {
				if (args[0].toLowerCase().equals("specimens")) {
					downloader.download(Type.SPECIMEN, args[1]);
				}
				else if (args[0].toLowerCase().equals("multimedia")) {
					downloader.download(Type.MULTIMEDIA, args[1]);
				}
				else {
					logger.error(USAGE);
				}
			}

			else {
				logger.error(USAGE);
			}

		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
			logger.error("Download did not complete successfully");
		}

	}

	public static enum Type
	{
		SPECIMEN, MULTIMEDIA
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsDownloader.class);
	private static final String USAGE = "USAGE: java CrsDownloader [specimens|multimedia [<resumption-token>]]";

	private final DocumentBuilder builder;


	public CrsDownloader() throws ParserConfigurationException
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		builder = builderFactory.newDocumentBuilder();
	}


	public void download(Type type, String resToken) throws Exception
	{
		String s = type == Type.SPECIMEN ? "specimens" : "multimedia";
		logger.info("Downloading " + s);

		if (resToken != null) {
			logger.info(String.format("Resuming with resumption token \"%s\"", resToken));
		}

		// Override/ignore some properties which are only relevant while
		// indexing (within CrsSpecimenImporter or CrsMultiMediaImporter):
		LoadUtil.getConfig().set("crs.save_local", "true");

		do {
			String xml;
			if (type == Type.SPECIMEN) {
				xml = CrsSpecimenImporter.callOaiService(resToken);
			}
			else {
				xml = CrsMultiMediaImporter.callOaiService(resToken);
			}
			Document doc = builder.parse(StringUtil.asInputStream(xml));
			if (!doc.getDocumentElement().getTagName().equals("OAI-PMH")) {
				throw new Exception("XML output is not OAI-PMH");
			}
			Element e = DOMUtil.getDescendant(doc.getDocumentElement(), "error");
			if (e == null) {
				logger.debug("Extracting resumption token from output");
				resToken = DOMUtil.getDescendantValue(doc, "resumptionToken");
				if (resToken != null) {
					logger.info("Resumption token used for next call: " + resToken);
				}
			}
			else {
				String msg = String.format("OAI Error (code=\"%s\"): \"%s\"", e.getAttribute("code"), e.getTextContent());
				throw new Exception(msg);
			}
		} while (resToken != null && resToken.trim().length() != 0);
		logger.info("Successfully downloaded " + s);
	}

}
