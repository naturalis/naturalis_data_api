package nl.naturalis.nba.dao.format;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for writing data set documents.
 * 
 * @author Ayco Holleman
 *
 */
public class FormatUtil {

	/**
	 * The empty string (&#34;&#34;)
	 */
	public static final String EMPTY_STRING = "";
	/**
	 * The date format used by Elasticsearch when calling
	 * {@code SearchHit.getSource()} (&#34;yyyy-MM-dd'T'HH:mm:ss.SSSZ&#34;).
	 * This call produces an instance of Map&lt;String,Object&gt; and apparently
	 * date fields end up as {@code String}s rather than {@code Date}s in this
	 * map.
	 */
	public static final SimpleDateFormat ES_DATE_TIME_FORMAT;
	/**
	 * The default date format used when writing dates (&#34;yyyy/MM/dd&#34;) to
	 * CSV files, DarwinCore archives, XML files, etc&#46;
	 */
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT;

	static {
		ES_DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
	}

	private FormatUtil()
	{
	}

	/**
	 * Reformats an {@link #ES_DATE_TIME_FORMAT Elasticsearch-formatted} date
	 * string to a date string formatted according to the
	 * {@link #DEFAULT_DATE_FORMAT default} date format.
	 * 
	 * @param esDate
	 * @return
	 */
	public static String formatDate(Date esDate)
	{
		return DEFAULT_DATE_FORMAT.format(esDate);
	}

}
