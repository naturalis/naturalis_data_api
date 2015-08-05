package nl.naturalis.nda.elasticsearch.load;

import static nl.naturalis.nda.elasticsearch.load.LoadConstants.SYSPROP_CONFIG_DIR;
import static org.apache.commons.io.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;

import org.domainobject.util.http.SimpleHttpHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMimeTypeCache implements MimeTypeCache {

	/**
	 * The separator between keys (UnitIDs) and values (mime types) in the cache
	 * file. Keys and values are each placed on a separate line in the cache
	 * file. Keys are on odd lines; values on even lines.
	 */
	protected static final byte[] NEWLINE_BYTES = "\n".getBytes(UTF_8);
	/**
	 * The size of the buffer used when loading the cache file into memory. The
	 * cache file is loaded in chunks of 64K at a time.
	 */
	protected static final int READ_BUFFER_SIZE = 1024 * 64;
	/**
	 * The mime type for JPEG images (image/jpeg), which are by far the most
	 * common media type in the medialib. To conserve memory, if a mime type in
	 * the cache file {@code equals} {@code JPEG}, {@code JPEG} should be added
	 * to the cache, rather than the original {@code String}.
	 */
	protected static final String JPEG = "image/jpeg";

	private static final Logger logger = LoggerFactory.getLogger(AbstractMimeTypeCache.class);

	private final SimpleHttpHead httpHead = new SimpleHttpHead();

	private final File cacheFile;
	private final int numEntries;

	private boolean changed = false;

	private int cacheHits = 0;
	private int medialibRequests = 0;
	private int requestFailures = 0;


	AbstractMimeTypeCache(String cacheFileName)
	{
		String ndaConfDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (ndaConfDir == null) {
			String fmt = "Missing system property \"%1$s\". Add -D%1$s=/path/to/conf/dir to command line arguments";
			throw new RuntimeException(String.format(fmt, SYSPROP_CONFIG_DIR));
		}
		File dir = new File(ndaConfDir);
		if (!dir.isDirectory()) {
			String fmt = "Invalid directory specified for property \"ndaConfDir\": \"%s\"";
			throw new RuntimeException(String.format(fmt, ndaConfDir));
		}
		cacheFile = new File(dir.getAbsolutePath() + '/' + cacheFileName);
		if (!cacheFile.isFile()) {
			String fmt = "Missing cache file (%s). You should put it in %s.";
			throw new RuntimeException(String.format(fmt, cacheFileName, ndaConfDir));
		}
		logger.info("Initializing mime type cache");
		numEntries = buildCache(cacheFile);
		logger.info(String.format("Initialization complete. Number of entries in cache: %s", numEntries));
	}


	@Override
	public void resetCounters()
	{
		cacheHits = 0;
		medialibRequests = 0;
		requestFailures = 0;
	}


	public String getMimeType(String unitID)
	{
		String mimetype = getEntry(unitID);
		if (mimetype == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Retrieving mime type for unitID \"%s\"", unitID));
			}
			String url = MEDIALIB_URL_START + unitID;
			++medialibRequests;
			mimetype = httpHead.setBaseUrl(url).execute().getHttpResponse().getFirstHeader("Content-Type").getValue();
			if (httpHead.isOK()) {
				addEntry(unitID, mimetype);
				changed = true;
			}
			else {
				++requestFailures;
				// We are still going to cache this UnitID, associating it
				// with an empty mime type, because it's probably not going
				// to get any better for this UnitID next time round, so no
				// use going out to the medialib again and again for this
				// UnitID.
				addEntry(unitID, "");
				if (logger.isDebugEnabled()) {
					String fmt = "Error retrieving mime type for URL %s: HTTP %s (%s). Mime type set to \"\"";
					logger.debug(String.format(fmt, url, httpHead.getStatus(), httpHead.getError()));
				}
			}
		}
		else {
			++cacheHits;
		}
		return mimetype;
	}


	@Override
	public int getSize()
	{
		return numEntries;
	}


	/**
	 * Get the number of times the request to the medialib for some reason
	 * failed to return HTTP 200 (OK)
	 * 
	 * @return
	 */
	@Override
	public int getRequestFailures()
	{
		return requestFailures;
	}


	/**
	 * Get the number of successful mime type lookups (without having to call
	 * the medialib).
	 * 
	 * @return
	 */
	@Override
	public int getCacheHits()
	{
		return cacheHits;
	}


	/**
	 * Get the number of times the mime type had to be retrieved by calling the
	 * medialib.
	 * 
	 * @return
	 */
	@Override
	public int getMedialibRequests()
	{
		return medialibRequests;
	}


	/**
	 * Closes the cache. Notably, if the cache has changed since it was
	 * instantiated, it is saved back to the file system. Therefore you should
	 * always call this method once you're done using the cache. Otherwise, new
	 * cache entries for new media objects will not get saved to the file
	 * system, causing repeated, expensive calls to the medialib.
	 */
	@Override
	public void close() throws IOException
	{
		if (changed) {
			saveCache(cacheFile);
			changed = false;
		}
		closeCache();
	}


	/**
	 * Load the cache file into memory and return the number of entries found in
	 * the cache file. Called in the constructor.
	 * 
	 * @param cacheFile
	 * @return The number of entries in the cache file.
	 */
	protected abstract int buildCache(File cacheFile);


	/**
	 * Add a new entry to the cache. Called in case of a cache miss.
	 * 
	 * @param unitID
	 * @param mimeType
	 */
	protected abstract void addEntry(String unitID, String mimeType);


	/**
	 * Look up the specified UnitID in the mimetype cache and, if found, return
	 * the mime type associated with it.
	 * 
	 * @param unitID
	 * @return
	 */
	protected abstract String getEntry(String unitID);


	/**
	 * Save the cache back to the cache file. Called if at least one new entry
	 * has been added to the cache.
	 * 
	 * @throws IOException
	 */
	protected abstract void saveCache(File cacheFile) throws IOException;


	/**
	 * Release all memory and resources associated with the cache.
	 * 
	 * @throws IOException
	 */
	protected abstract void closeCache() throws IOException;
}
