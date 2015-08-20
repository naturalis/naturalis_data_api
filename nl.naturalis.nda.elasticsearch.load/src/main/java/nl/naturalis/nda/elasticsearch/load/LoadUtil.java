package nl.naturalis.nda.elasticsearch.load;

import static org.domainobject.util.StringUtil.zpad;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.slf4j.Logger;

/**
 * Utility class providing common functionality for all import programs.
 * 
 * @author Ayco Holleman
 *
 */
public final class LoadUtil {

	@SuppressWarnings("unused")
	private static final Logger logger = Registry.getInstance().getLogger(LoadUtil.class);

	private LoadUtil()
	{
	}

	public static void truncate(String luceneType, SourceSystem sourceSystem)
	{
		IndexNative indexManager = Registry.getInstance().getNbaIndexManager();
		indexManager.deleteWhere(luceneType, "sourceSystem.code", sourceSystem.getCode());
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

	/**
	 * Equivalent to {@code URLEncoder.encode(raw, "UTF-8")} suppressing the
	 * {@code UnsupportedEncodingException}.
	 * 
	 * @param raw
	 * @return
	 */
	public static String urlEncode(String raw)
	{
		try {
			return URLEncoder.encode(raw, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			// Won't happen with UTF-8
			return null;
		}
	}

}
