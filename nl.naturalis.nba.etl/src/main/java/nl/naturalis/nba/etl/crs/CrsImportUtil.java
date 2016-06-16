package nl.naturalis.nba.etl.crs;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.http.SimpleHttpGet;
import org.joda.time.DateTime;

import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.etl.ETLRegistry;

/**
 * Class providing common functionality for CRS imports.
 * 
 * @author Ayco Holleman
 *
 */
class CrsImportUtil {

	static final ConfigObject config;
	static final SimpleDateFormat oaiDateFormatter;

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(CrsImportUtil.class);
		config = DAORegistry.getInstance().getConfiguration();
		oaiDateFormatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
	}

	private CrsImportUtil()
	{
	}

	/**
	 * Generates and executes an initial request to the CRS OAI service for
	 * specimens and returns the response as a byte array.
	 * 
	 * @param fromDate
	 * @param untilDate
	 * @return
	 */
	static byte[] callSpecimenService(Date fromDate, Date untilDate)
	{
		String url = config.required("crs.specimens.url.initial");
		if (fromDate != null)
			url += "&from=" + oaiDateFormatter.format(fromDate);
		if (untilDate != null)
			url += "&until=" + oaiDateFormatter.format(untilDate);
		return callService(url);
	}

	/**
	 * Calls the CRS OAI service for specimens using the specified resumption
	 * token and returns the response as a byte array.
	 * 
	 * @param resumptionToken
	 * @return
	 */
	static byte[] callSpecimenService(String resumptionToken)
	{
		String url;
		if (resumptionToken == null) {
			url = config.required("crs.specimens.url.initial");
			int maxAge = config.required("crs.max_age", int.class);
			if (maxAge != 0) {
				DateTime now = new DateTime();
				DateTime wayback = now.minusHours(maxAge);
				url += "&from=" + oaiDateFormatter.format(wayback.toDate());
			}
		}
		else {
			String urlPattern = config.required("crs.specimens.url.resume");
			url = String.format(urlPattern, resumptionToken);
		}
		return callService(url);
	}

	/**
	 * Generates and executes an initial request to the CRS OAI service for
	 * specimens and returns the response as a byte array.
	 * 
	 * @param fromDate
	 * @param untilDate
	 * @return
	 */
	static byte[] callMultimediaService(Date fromDate, Date untilDate)
	{
		String url = config.required("crs.multimedia.url.initial");
		if (fromDate != null) {
			url += "&from=" + oaiDateFormatter.format(fromDate);
		}
		if (untilDate != null) {
			url += "&until=" + oaiDateFormatter.format(untilDate);
		}
		return callService(url);
	}

	/**
	 * Calls the CRS OAI service for multimedia using the specified resumption
	 * token and returns the response as a byte array.
	 * 
	 * @param resumptionToken
	 * @return
	 */
	static byte[] callMultimediaService(String resumptionToken)
	{
		String url;
		if (resumptionToken == null) {
			url = config.required("crs.multimedia.url.initial");
			int maxAge = config.required("crs.max_age", int.class);
			if (maxAge != 0) {
				DateTime now = new DateTime();
				DateTime wayback = now.minusHours(maxAge);
				url += "&from=" + oaiDateFormatter.format(wayback.toDate());
			}
		}
		else {
			url = String.format(config.required("crs.multimedia.url.resume"), resumptionToken);
		}
		return callService(url);
	}

	private static byte[] callService(String url)
	{
		logger.info("Calling service: " + url);
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}

}
