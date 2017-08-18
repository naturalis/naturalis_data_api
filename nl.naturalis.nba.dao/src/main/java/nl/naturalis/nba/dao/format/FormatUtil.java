package nl.naturalis.nba.dao.format;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
	 * The empty string (&#34;&#34;)
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * Formats a date using pattern "yyyy/MM/dd".
	 * 
	 * @param esDate
	 * @return
	 */
	public static String formatDate(OffsetDateTime esDate)
	{
		/*
		 * NB Do not store the SimpleDateFormat in a private static final field.
		 * SimpleDateFormat is not thread-save, which becomes very relevant with
		 * multiple concurrent DwCA downloads.
		 */
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return esDate.format(formatter);
	}

	private FormatUtil()
	{
	}

}
