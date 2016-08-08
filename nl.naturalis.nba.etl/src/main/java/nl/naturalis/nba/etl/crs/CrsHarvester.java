package nl.naturalis.nba.etl.crs;

import static nl.naturalis.nba.etl.crs.CrsImportUtil.callMultimediaService;
import static nl.naturalis.nba.etl.crs.CrsImportUtil.callSpecimenService;
import static org.domainobject.util.FileUtil.setContents;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.dao.es.DaoRegistry;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLRuntimeException;

/**
 * Harvests the CRS OAI service and saves its output to files on the local file
 * system. These files can then be processed further by
 * {@link CrsSpecimenImportOffline} and {@link CrsMultiMediaImportOffline}.
 * 
 * @author Ayco Holleman
 * 
 */
public class CrsHarvester {

	public static void main(String[] args)
	{
		try {
			CrsHarvester downloader = new CrsHarvester();
			if (args.length == 0) {
				downloader.downloadSpecimens(null, null);
				downloader.downloadMultiMedia(null, null);
			}
			if (args.length > 3)
				logger.error(usage);
			else {
				String type = args[0].toLowerCase();
				Date fromDate = null;
				Date untilDate = null;
				if (args.length > 1)
					fromDate = parseDate(args[1]);
				if (args.length > 2)
					untilDate = parseDate(args[2]);
				if (type.equals("specimens"))
					downloader.downloadSpecimens(fromDate, untilDate);
				else if (type.equals("multimedia"))
					downloader.downloadMultiMedia(fromDate, untilDate);
				else
					logger.error(usage);
			}
		}
		catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
	}

	private static final Logger logger;

	/*
	 * Date format used for OAI requests.
	 */
	private static final SimpleDateFormat oaiDateFormat;
	/*
	 * Date format used for file names, when saving the response from the OAI
	 * service.
	 */
	static final SimpleDateFormat fileNameDateFormat;
	/*
	 * Date format that can be used to manually specify a from and until date,
	 * besides the other two.
	 */
	static final SimpleDateFormat simpleDateFormat;

	private static final String usage;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsHarvester.class);
		oaiDateFormat = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
		fileNameDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		usage = "USAGE: java CrsDownloader [[[specimens|multimedia] fromdate] untildate]";
	}

	public CrsHarvester()
	{
	}

	/**
	 * Harvests the CRS OAI service for specimens.
	 * 
	 * @param fromDate
	 * @param untilDate
	 */
	public void downloadSpecimens(Date fromDate, Date untilDate)
	{
		logger.info("Downloading specimens");
		if (fromDate == null) {
			fromDate = checkAdminFile("specimens");
		}
		/*
		 * This is the date we are going to write to .crs-specimens.oai file in
		 * the conf directory, so next time we harvest we can use that date as
		 * the from date.
		 */
		Date successDate = untilDate == null ? new Date() : untilDate;
		int numCalls = 0;
		byte[] response = callSpecimenService(fromDate, untilDate);
		setContents(getLocalPath("specimens", fromDate, numCalls++), response);
		String resumptionToken = getResumptionToken(response);
		while (resumptionToken != null) {
			response = callSpecimenService(resumptionToken);
			setContents(getLocalPath("specimens", fromDate, numCalls++), response);
			resumptionToken = getResumptionToken(response);
		}
		String from = fileNameDateFormat.format(successDate);
		setContents(getAdminFile("specimens"), from);
		logger.info("Successfully downloaded specimens");
	}

	/**
	 * Harvests the CRS OAI service for multimedia.
	 * 
	 * @param fromDate
	 * @param untilDate
	 */
	public void downloadMultiMedia(Date fromDate, Date untilDate)
	{
		logger.info("Downloading multimedia");
		if (fromDate == null) {
			fromDate = checkAdminFile("multimedia");
		}
		/*
		 * This is the date we are going to write to .crs-multimedia.oai file in
		 * the conf directory, so next time we harvest we can use that date as
		 * the from date.
		 */
		Date successDate = untilDate == null ? new Date() : untilDate;
		int numCalls = 0;
		byte[] response = callMultimediaService(fromDate, untilDate);
		setContents(getLocalPath("multimedia", fromDate, numCalls++), response);
		String resumptionToken = getResumptionToken(response);
		while (resumptionToken != null) {
			response = callMultimediaService(resumptionToken);
			setContents(getLocalPath("multimedia", fromDate, numCalls++), response);
			resumptionToken = getResumptionToken(response);
		}
		String from = fileNameDateFormat.format(successDate);
		setContents(getAdminFile("multimedia"), from);
		logger.info("Successfully downloaded multimedia");
	}

	public String getResumptionToken(byte[] xml)
	{
		return new CrsExtractor(xml, null).getResumptionToken();
	}

	private static File getLocalPath(String type, Date fromDate, int callNum)
	{
		StringBuilder sb = new StringBuilder(100);
		ConfigObject cfg = DaoRegistry.getInstance().getConfiguration();
		String dir = cfg.getDirectory("crs.data_dir").getAbsolutePath();
		sb.append(dir).append('/').append(type).append('.');
		if (fromDate == null)
			sb.append("00000000000000");
		else
			sb.append(fileNameDateFormat.format(fromDate));
		sb.append('.');
		sb.append(new DecimalFormat("000000").format(callNum));
		sb.append(".oai.xml");
		logger.info("Saving response to " + sb);
		return new File(sb.toString());
	}

	private static Date checkAdminFile(String type)
	{
		File f = getAdminFile(type);
		if (!f.exists()) {
			String fmt = "File not found: %s. Will harvest from scratch";
			logger.info(String.format(fmt, f.getAbsolutePath()));
			return null;
		}
		try {
			String date = FileUtil.getContents(f);
			String fmt = "Found file %s. Will harvest from %s";
			String msg = String.format(fmt, f.getAbsolutePath(), date);
			logger.info(msg);
			return fileNameDateFormat.parse(date);
		}
		catch (ParseException e) {
			throw new ETLRuntimeException(e);
		}
	}

	private static File getAdminFile(String type)
	{
		File dir = DaoRegistry.getInstance().getConfigurationDirectory();
		return FileUtil.newFile(dir, ".crs-" + type + ".oai");
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
					String fmt = "Invalid date: \"%s\"";
					String msg = String.format(fmt, date);
					throw new ETLRuntimeException(msg);
				}
			}
		}
	}

}
