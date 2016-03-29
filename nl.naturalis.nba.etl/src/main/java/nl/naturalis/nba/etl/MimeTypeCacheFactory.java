package nl.naturalis.nba.etl;

import org.apache.logging.log4j.Logger;


/**
 * Produces a {@link MimeTypeCache} instances based on the value of a system
 * property named "mimetypecache.type". If absent, or if it has value "map", a
 * {@link MapMimeTypeCache} is returned to the client, otherwise an
 * {@link ArrayMimeTypeCache}.
 * 
 * @author Ayco Holleman
 *
 */
public class MimeTypeCacheFactory {

	private static final String CACHE_FILE_NAME = "mimetypes.cache";
	private static final Logger logger = Registry.getInstance().getLogger(MimeTypeCacheFactory.class);

	private static MimeTypeCacheFactory instance;

	/**
	 * Get an instance of the factory class.
	 * 
	 * @return
	 */
	public static MimeTypeCacheFactory getInstance()
	{
		if (instance == null) {
			instance = new MimeTypeCacheFactory();
		}
		return instance;
	}

	private final MimeTypeCache cache;

	private MimeTypeCacheFactory()
	{
		String propName = "mimetypecache.type";
		String propVal = System.getProperty(propName, "map");
		if (propVal.equalsIgnoreCase("array")) {
			logType(propName, propVal);
			cache = new ArrayMimeTypeCache(CACHE_FILE_NAME);
		}
		else if (propVal.equalsIgnoreCase("map")) {
			logType(propName, propVal);
			cache = new MapMimeTypeCache(CACHE_FILE_NAME);
		}
		else {
			String fmt = "Invalid value for mimetypecache.type: \"%s\". Valid cache types: \"map\" (default), \"array\"";
			throw new RuntimeException(String.format(fmt, propVal));
		}
	}

	/**
	 * Get a mime type cache instance.
	 * 
	 * @return
	 */
	public MimeTypeCache getCache()
	{
		return cache;
	}

	private static void logType(String propName, String propVal)
	{
		String s = propVal.equalsIgnoreCase("map") ? propVal + " (default)" : propVal;
		logger.info(String.format("Creating mime type cache. Type of cache: \"%s\"", s));
		s = propVal.equalsIgnoreCase("map") ? "array" : "map";
		String fmt = "To change cache type, extend JAVA_OPTS in include.sh: -D%s=%s";
		logger.info(String.format(fmt, propName, s));
	}

}
