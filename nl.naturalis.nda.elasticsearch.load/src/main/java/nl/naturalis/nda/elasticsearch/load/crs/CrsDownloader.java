package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.ExceptionUtil;
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

		if(args.length == 0) {
			downloader.download(Type.SPECIMEN, null, 0);
			downloader.download(Type.MULTIMEDIA, null, 0);
		}
		
		else if(args.length == 1) {
			if(args[0].toLowerCase().equals("specimens")) {
				downloader.download(Type.SPECIMEN, null, 0);
			}
			else if(args[0].toLowerCase().equals("multimedia")) {
				downloader.download(Type.MULTIMEDIA, null, 0);
			}
			else {
				logger.error(USAGE);
			}
		}
		
		else if(args.length == 3) {
			if(args[0].toLowerCase().equals("specimens")) {
				downloader.download(Type.SPECIMEN, args[1], Integer.parseInt(args[2]));
			}
			else if(args[0].toLowerCase().equals("multimedia")) {
				downloader.download(Type.MULTIMEDIA, args[1], Integer.parseInt(args[2]));
			}
			else {
				logger.error(USAGE);
			}
		}
		
		else {
			logger.error(USAGE);
		}

	}

	public static enum Type
	{
		SPECIMEN, MULTIMEDIA
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsDownloader.class);
	private static final String USAGE = "USAGE: java CrsDownloader [ specimens|multimedia [<resumption-token> <batchNo>] ]";

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


	public void download(Type type, String resToken, int batch)
	{
		String s = type == Type.SPECIMEN ? "specimens" : "multimedia";
		logger.info("Downloading " + s);
		
		if(resToken == null) {
			logger.info("Starting from scratch");
		}
		else {
			logger.info(String.format("Resuming with resumption token \"%s\"", resToken));
		}
		
		// Override/ignore some properties which are only relevant while
		// indexing (within CrsSpecimenImporter or CrsMultiMediaImporter):
		LoadUtil.getConfig().set("crs.save_local", "true");
		LoadUtil.getConfig().set("crs.max_age", "0");
		
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
