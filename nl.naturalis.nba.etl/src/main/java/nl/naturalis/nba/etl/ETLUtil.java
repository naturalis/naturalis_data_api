package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.LoadConstants.PURL_SERVER_BASE_URL;
import static nl.naturalis.nba.utils.StringUtil.zpad;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.DaoRegistry;

/**
 * Utility class providing common functionality used throughout this library.
 * 
 * @author Ayco Holleman
 *
 */
public final class ETLUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(Loader.class);

	private static final URIBuilder purlBuilder;
	private static final String purlSpecimenPath;

	static {
		purlBuilder = getPurlBuilder();
		purlSpecimenPath = purlBuilder.getPath() + "/naturalis/specimen/";
	}

	private ETLUtil()
	{
	}

	/**
	 * Get root cause of the specified {@code Throwable}. Returns the
	 * {@code Throwable} itself if it doesn't have a cause.
	 * 
	 * @param t
	 * @return
	 */
	public static Throwable getRootCause(Throwable t)
	{
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t;
	}

	/**
	 * Logs a nice message about how long an import program took.
	 * 
	 * @param logger
	 *            The logger to log to
	 * @param cls
	 *            The main class of the import program
	 * @param start
	 *            The start of the program
	 */
	public static void logDuration(Logger logger, Class<?> cls, long start)
	{
		logger.info(cls.getSimpleName() + " took " + getDuration(start));
	}

	/**
	 * Get the duration between {@code start} and now, formatted as HH:mm:ss.
	 * 
	 * @param start
	 * @return
	 */
	public static String getDuration(long start)
	{
		return getDuration(start, System.currentTimeMillis());
	}

	/**
	 * Get the duration between {@code start} and {@code end}, formatted as
	 * HH:mm:ss.
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static String getDuration(long start, long end)
	{
		int millis = (int) (end - start);
		int hours = millis / (60 * 60 * 1000);
		millis = millis % (60 * 60 * 1000);
		int minutes = millis / (60 * 1000);
		millis = millis % (60 * 1000);
		int seconds = millis / 1000;
		return zpad(hours, 2, ":") + zpad(minutes, 2, ":") + zpad(seconds, 2);
	}

	public static String getSpecimenPurl(String unitID)
	{
		try {
			purlBuilder.setPath(purlSpecimenPath + unitID);
			return purlBuilder.build().toString();
		}
		catch (URISyntaxException e) {
			throw new ETLRuntimeException(e);
		}
	}

	public static Logger getLogger(Class<?> forClass)
	{
		return ETLRegistry.getInstance().getLogger(forClass);
	}

	private static URIBuilder getPurlBuilder()
	{
		String value = null;
		try {
			String property = "purl.baseurl";
			value = DaoRegistry.getInstance().getConfiguration().get(property,
					PURL_SERVER_BASE_URL);
			return new URIBuilder(value);
		}
		catch (URISyntaxException e) {
			String fmt = "Could not create URIBuilder for PURL base URL \"%s\": %s";
			String msg = String.format(fmt, value, e.getMessage());
			throw new ETLRuntimeException(msg);
		}
	}


}
