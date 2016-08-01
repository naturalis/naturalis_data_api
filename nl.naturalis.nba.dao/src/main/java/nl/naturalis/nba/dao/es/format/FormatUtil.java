package nl.naturalis.nba.dao.es.format;

public class FormatUtil {

	public static final String EMPTY_STRING = "";

	private FormatUtil()
	{
	}

	public static String nvl(Object o)
	{
		if (o == null)
			return EMPTY_STRING;
		return String.valueOf(o);
	}

	public static String nvl(String s)
	{
		if (s == null)
			return EMPTY_STRING;
		return s;
	}

}
