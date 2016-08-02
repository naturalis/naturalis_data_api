package nl.naturalis.nba.dao.es.format;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatUtil {

	public static final String EMPTY_STRING = "";
	public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private FormatUtil()
	{
	}

	public static String formatDate(Date date)
	{
		return DEFAULT_DATE_FORMAT.format(date);
	}

}
