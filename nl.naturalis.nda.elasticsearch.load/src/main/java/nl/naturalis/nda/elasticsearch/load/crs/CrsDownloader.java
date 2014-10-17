package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.IOException;
import java.text.DecimalFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.FileUtil;
import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Harvests the CRS OAI interface and saves its output to local files. Since the
 * CRS OAI service is very slow, it pays to first store its output into files,
 * and then process those, if we often need to do a full harvest.
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

	private final DecimalFormat decimalFormat;
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
		decimalFormat = new DecimalFormat();
		decimalFormat.setMinimumIntegerDigits(6);
		decimalFormat.setGroupingUsed(false);
	}


	public void download(Type type)
	{
		String s = type == Type.SPECIMEN ? "specimens" : "multimedia";
		logger.info("Downloading " + s);
		String resToken = null;
		do {
			String xml = type == Type.SPECIMEN ? CrsSpecimenImporter.getXML(resToken) : CrsMultiMediaImporter.getXML(resToken);
			String saveTo = type == Type.SPECIMEN ? CrsSpecimenImporter.getLocalFile(resToken) : CrsMultiMediaImporter.getLocalFile(resToken);
			logger.info("Saving output to file " + saveTo);
			FileUtil.setContents(saveTo, xml);
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
