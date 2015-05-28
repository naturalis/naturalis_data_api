package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.naturalis.nda.elasticsearch.load.LoadUtil;

import org.domainobject.util.DOMUtil;
import org.domainobject.util.FileUtil;
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
				downloader.downloadSpecimens();
				downloader.downloadMultiMedia();
			}

			else if (args.length == 1) {
				if (args[0].toLowerCase().equals("specimens")) {
					downloader.downloadSpecimens();
				}
				else if (args[0].toLowerCase().equals("multimedia")) {
					downloader.downloadMultiMedia();
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

		logger.info("Ready");

	}

	public static enum Type
	{
		SPECIMEN, MULTIMEDIA
	}

	private static final Logger logger = LoggerFactory.getLogger(CrsDownloader.class);
	private static final String USAGE = "USAGE: java CrsDownloader [specimens|multimedia [<resumption-token>]]";

	private final DocumentBuilder builder;


	public CrsDownloader()
	{
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setNamespaceAware(false);
		try {
			builder = builderFactory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}


	public void downloadSpecimens()
	{
		logger.info("Downloading specimens");
		String xml = CrsSpecimenImporter.callOaiService(getFromDate(Type.SPECIMEN));
		xml = CrsImportUtil.cleanupXml(xml);
		String resumptionToken = saveXml(Type.SPECIMEN, xml);
		while (resumptionToken != null && resumptionToken.trim().length() != 0) {
			xml = CrsSpecimenImporter.callOaiService(resumptionToken);
			xml = CrsImportUtil.cleanupXml(xml);
			resumptionToken = saveXml(Type.SPECIMEN, xml);
		}
		logger.info("Successfully downloaded specimens");
	}


	public void downloadMultiMedia()
	{
		logger.info("Downloading multimedia");
		String xml = CrsMultiMediaImporter.callOaiService(getFromDate(Type.MULTIMEDIA));
		xml = CrsImportUtil.cleanupXml(xml);
		String resumptionToken = saveXml(Type.MULTIMEDIA, xml);
		while (resumptionToken != null && resumptionToken.trim().length() != 0) {
			xml = CrsMultiMediaImporter.callOaiService(resumptionToken);
			xml = CrsImportUtil.cleanupXml(xml);
			resumptionToken = saveXml(Type.MULTIMEDIA, xml);
		}
		logger.info("Successfully downloaded multimedia");
	}


	public String saveXml(Type type, String xml)
	{
		Document doc;
		try {
			doc = builder.parse(StringUtil.asInputStream(xml));
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
		if (!doc.getDocumentElement().getTagName().equals("OAI-PMH")) {
			throw new RuntimeException("XML output is not OAI-PMH");
		}
		Element e = DOMUtil.getDescendant(doc.getDocumentElement(), "error");
		if (e != null) {
			String msg = String.format("OAI Error (code=\"%s\"): \"%s\"", e.getAttribute("code"), e.getTextContent());
			throw new RuntimeException(msg);
		}
		File file = getLocalPath(type, doc);
		logger.info("Saving XML to " + file.getAbsolutePath());
		FileUtil.setContents(file, xml);
		return DOMUtil.getDescendantValue(doc, "resumptionToken");
	}

	private static final SimpleDateFormat oaiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");


	private static File getLocalPath(Type type, Document doc)
	{
		Element e = DOMUtil.getChild(doc.getDocumentElement(), "ListRecords");
		if (e == null) {
			throw new RuntimeException("Missing <ListRecords> element in OAI output");
		}
		List<Element> records = DOMUtil.getChildren(e, "record");
		try {
			String s = DOMUtil.getDescendantValue(records.get(0), "datestamp");
			String date0 = fileNameDateFormat.format(oaiDateFormat.parse(s));
			s = DOMUtil.getDescendantValue(records.get(records.size() - 1), "datestamp");
			String date1 = fileNameDateFormat.format(oaiDateFormat.parse(s));
			s = type == Type.SPECIMEN ? "specimens" : "multimedia";
			String fileName = s + "." + date0 + "." + date1 + ".oai.xml";
			File dir = LoadUtil.getConfig().getDirectory("crs.local_dir");
			return new File(dir.getAbsolutePath() + "/" + fileName);
		}
		catch (ParseException exc) {
			throw new RuntimeException(exc);
		}
	}


	private static Date getFromDate(final Type type)
	{
		File dir = LoadUtil.getConfig().getDirectory("crs.local_dir");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name)
			{
				if (!name.endsWith(".oai.xml")) {
					return false;
				}
				if (type == Type.SPECIMEN && name.startsWith("specimens")) {
					return true;
				}
				if (type == Type.MULTIMEDIA && name.startsWith("multimedia")) {
					return true;
				}
				return false;
			}
		});
		if (files.length == 0) {
			// There are no XML files yet in the directory
			return null;
		}
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2)
			{
				// Reverse sort!
				return o2.getName().compareTo(o1.getName());
			}
		});
		/*
		 * File names look like this: specimens.fromdate.todate.oai.xml
		 */
		String fromDateString = files[0].getName().split("\\.")[2];
		try {
			return fileNameDateFormat.parse(fromDateString);
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
