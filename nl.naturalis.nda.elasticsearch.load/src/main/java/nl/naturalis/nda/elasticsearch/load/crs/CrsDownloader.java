package nl.naturalis.nda.elasticsearch.load.crs;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
 * @author Ayco Holleman
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
				downloader.downloadSpecimens(null, null);
				downloader.downloadMultiMedia(null, null);
			}

			if (args.length > 3) {
				logger.error(USAGE);
			}

			else {
				String type = args[0].toLowerCase();
				Date fromDate = null;
				Date untilDate = null;
				if (args.length > 1) {
					fromDate = parseDate(args[1]);
				}
				if (args.length > 2) {
					untilDate = parseDate(args[2]);
				}
				if (type.equals("specimens")) {
					downloader.downloadSpecimens(fromDate, untilDate);
				}
				else if (type.equals("multimedia")) {
					downloader.downloadMultiMedia(fromDate, untilDate);
				}
				else {
					logger.error(USAGE);
				}
			}

		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}

	}

	public static enum Type
	{
		SPECIMEN, MULTIMEDIA
	}

	/*
	 * Date format used for OAI requests
	 */
	private static final String OAI_DATE_FORMAT_PATTERN = "yyyy-MM-dd\'T\'HH:mm:ss\'Z\'";
	/*
	 * Date format used for file names (downloads of the OAI XML response)
	 */
	private static final String FILENAME_DATE_FORMAT_PATTERN = "yyyyMMddHHmmss";
	/*
	 * Date format that can be used to manually specify a from and until date
	 * (besides the other two)
	 */
	private static final String SIMPLE_DATE_FORMAT_PATTERN = "yyyyMMdd";

	static final SimpleDateFormat oaiDateFormat = new SimpleDateFormat(OAI_DATE_FORMAT_PATTERN);
	static final SimpleDateFormat fileNameDateFormat = new SimpleDateFormat(FILENAME_DATE_FORMAT_PATTERN);
	static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SIMPLE_DATE_FORMAT_PATTERN);

	private static final Logger logger = LoggerFactory.getLogger(CrsDownloader.class);
	private static final String USAGE = "USAGE: java CrsDownloader [[[specimens|multimedia] fromdate] untildate]";

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


	public void downloadSpecimens(Date fromDate, Date untilDate)
	{
		logger.info("Downloading specimens");
		if (fromDate == null) {
			fromDate = getFromDateFromAdminFile(Type.SPECIMEN);
		}
		if (untilDate == null) {
			untilDate = new Date();
		}
		int request = 0;
		String xml = CrsSpecimenImporter.callOaiService(fromDate, untilDate);
		String resumptionToken = saveXml(Type.SPECIMEN, xml, fromDate, request++);
		while (resumptionToken != null && resumptionToken.trim().length() != 0) {
			xml = CrsSpecimenImporter.callOaiService(resumptionToken);
			resumptionToken = saveXml(Type.SPECIMEN, xml, fromDate, request++);
		}
		/*
		 * If we make it to here, the next time we harvest we can take the
		 * current "until" date as the new "from" date. Otherwise we have no
		 * choice but to start from the original date in the admin file. In
		 * other words, if we make it to here, we can legitimately overwrite the
		 * "from" date in the admin file.
		 */
		FileUtil.setContents(getAdminFile(Type.SPECIMEN), fileNameDateFormat.format(untilDate));
		logger.info("Successfully downloaded specimens");
	}


	public void downloadMultiMedia(Date fromDate, Date untilDate)
	{
		logger.info("Downloading multimedia");
		if (fromDate == null) {
			fromDate = getFromDateFromAdminFile(Type.MULTIMEDIA);
		}
		if (untilDate == null) {
			untilDate = new Date();
		}
		int request = 0;
		String xml = CrsMultiMediaImporter.callOaiService(fromDate, untilDate);
		String resumptionToken = saveXml(Type.MULTIMEDIA, xml, fromDate, request++);
		while (resumptionToken != null && resumptionToken.trim().length() != 0) {
			xml = CrsMultiMediaImporter.callOaiService(resumptionToken);
			resumptionToken = saveXml(Type.MULTIMEDIA, xml, fromDate, request++);
		}
		FileUtil.setContents(getAdminFile(Type.SPECIMEN), fileNameDateFormat.format(untilDate));
		logger.info("Successfully downloaded multimedia");
	}


	public String saveXml(Type type, String xml, Date fromDate, int request)
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
		File file = getLocalPath(type, fromDate, request);
		logger.info("Saving XML to " + file.getAbsolutePath());
		FileUtil.setContents(file, xml);
		return DOMUtil.getDescendantValue(doc, "resumptionToken");
	}


	private static File getLocalPath(Type type, Date fromDate, int request)
	{
		String dir = LoadUtil.getConfig().getDirectory("crs.local_dir").getAbsolutePath();
		String typeString = type == Type.SPECIMEN ? "specimens" : "multimedia";
		String dateString = fileNameDateFormat.format(fromDate);
		String requestString = new DecimalFormat("000000").format(request);
		String path = dir + "/" + typeString + "." + dateString + "." + requestString + ".oai.xml";
		return new File(path);
	}


	private static Date getFromDateFromAdminFile(Type type)
	{
		File adminFile = getAdminFile(type);
		if (!adminFile.exists()) {
			return null;
		}
		try {
			return fileNameDateFormat.parse(FileUtil.getContents(adminFile));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}


	private static File getAdminFile(Type type)
	{
		File dir = LoadUtil.getConfig().getDirectory("crs.local_dir");
		return new File(dir.getAbsolutePath() + "/." + type.name().toLowerCase() + ".oai-admin");
	}


	private static Date parseDate(String date)
	{
		try {
			return oaiDateFormat.parse(date);
		}
		catch (ParseException e) {
			try {
				return fileNameDateFormat.parse(date);
			}
			catch (ParseException e1) {
				try {
					return simpleDateFormat.parse(date);
				}
				catch (ParseException e2) {
					String fmt = "Invalid date: \"%s\". Validate date patterns: \"%s\", \"%s\", \"%s\"";
					String msg = String.format(fmt, date, OAI_DATE_FORMAT_PATTERN, FILENAME_DATE_FORMAT_PATTERN, SIMPLE_DATE_FORMAT_PATTERN);
					throw new RuntimeException(msg);
				}
			}
		}
	}

}
