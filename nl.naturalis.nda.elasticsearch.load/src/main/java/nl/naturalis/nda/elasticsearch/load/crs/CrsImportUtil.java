package nl.naturalis.nda.elasticsearch.load.crs;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nda.elasticsearch.load.Registry;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.http.SimpleHttpGet;
import org.joda.time.DateTime;
import org.slf4j.Logger;

class CrsImportUtil {

	static final ConfigObject config;
	static final SimpleDateFormat oaiDateFormatter;

	private static final Logger logger;

	static {
		logger = Registry.getInstance().getLogger(CrsImportUtil.class);
		config = Registry.getInstance().getConfig();
		oaiDateFormatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss\'Z\'");
	}

	private CrsImportUtil()
	{
	}

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

	static byte[] callSpecimenService(Date fromDate, Date untilDate)
	{
		String url = config.required("crs.specimens.url.initial");
		if (fromDate != null)
			url += "&from=" + oaiDateFormatter.format(fromDate);
		if (untilDate != null)
			url += "&until=" + oaiDateFormatter.format(untilDate);
		return callService(url);
	}

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

	private static byte[] callService(String url)
	{
		logger.info("Calling service: " + url);
		return new SimpleHttpGet().setBaseUrl(url).execute().getResponseBody();
	}

}
