package nl.naturalis.nba.dao.format;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoUtil;

/**
 * Utility class for writing data set documents.
 * 
 * @author Ayco Holleman
 *
 */
public class FormatUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = DaoUtil.getLogger(FormatUtil.class);

	/**
	 * The empty string (&#34;&#34;).
	 */
	public static final String EMPTY_STRING = "";

	private static final DateTimeFormatter dwcaDateFormat = DateTimeFormatter
			.ofPattern("yyyy/MM/dd");

	/**
	 * Formats dates using pattern "yyyy/MM/dd".
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(OffsetDateTime date)
	{
		return date.format(dwcaDateFormat);
	}

	private FormatUtil()
	{
	}

}
