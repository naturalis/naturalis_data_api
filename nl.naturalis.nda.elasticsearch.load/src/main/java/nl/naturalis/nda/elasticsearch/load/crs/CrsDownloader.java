package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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

		CrsDownloader downloader = new CrsDownloader();
		String prop = System.getProperty("specimens", "1");
		if (prop.equals("1") || prop.equalsIgnoreCase("true")) {
			downloader.download(Type.SPECIMEN);
		}
		prop = System.getProperty("multimedia", "1");
		if (prop.equals("1") || prop.equalsIgnoreCase("true")) {
			downloader.download(Type.MULTIMEDIA);
		}
		logger.info("Ready");
	}

	public static enum Type
	{
		SPECIMEN, MULTIMEDIA
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsDownloader.class);

	private final DocumentBuilder builder;


	public CrsDownloader()
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	public void download(Type type)
	{
		String s = type == Type.SPECIMEN ? "specimens" : "multimedia";
		logger.info("Downloading " + s);
		String resToken = null;
		int batch = 0;
		// Override/ignore (only relevant within CrsSpecimenImporter
		// and CrsMultiMediaImporter)
		LoadUtil.getConfig().set("crs.save_local", "true");
		do {
			String xml;
			if (type == Type.SPECIMEN) {
				xml = CrsSpecimenImporter.callOaiService(resToken, batch++);
			}
			else {
				xml = CrsMultiMediaImporter.callOaiService(resToken, batch++);
			}
			logger.info("Extracting resumption token from output");
			try {
				Document doc = builder.parse(StringUtil.asInputStream(xml));
				resToken = DOMUtil.getDescendantValue(doc, "resumptionToken");
				if (resToken != null) {
					logger.info("Resumption token used for next call: " + resToken);
				}
			}
			catch (SAXException | IOException e) {
				throw ExceptionUtil.smash(e);
			}
		} while (resToken != null);
		logger.info("Successfully downloaded " + s);
	}

}
