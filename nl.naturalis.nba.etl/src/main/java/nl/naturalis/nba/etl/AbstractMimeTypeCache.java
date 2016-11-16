package nl.naturalis.nba.etl;

import static org.apache.commons.io.Charsets.UTF_8;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.FileUtil;
import org.domainobject.util.http.SimpleHttpHead;

import nl.naturalis.nba.dao.DaoRegistry;

/**
 * Abstract base class for mimetype caches. Provides the public interface of a
 * mimetype cache, while delegating the heavy lifting of building the cache to
 * subclasses.
 * 
 * @author Ayco Holleman
 *
 */
public abstract class AbstractMimeTypeCache implements MimeTypeCache {

	/**
	 * The separator between keys (UnitIDs) and values (mime types) in the cache
	 * file. Keys and values are each placed on a separate line in the cache
	 * file. Keys are on odd lines; values on even lines.
	 */
	protected static final byte[] NEWLINE_BYTES = "\n".getBytes(UTF_8);
	/**
	 * The size of the buffer used to load the cache file into memory. The cache
	 * file is loaded in chunks of 64K at a time.
	 */
	protected static final int READ_BUFFER_SIZE = 1024 * 64;
	/**
	 * The mime type for JPEG images (image/jpeg), which is by far the most
	 * common media type in the medialib. To conserve memory, if a mime type in
	 * the cache file {@code equals} {@code JPEG}, {@code JPEG} should be added
	 * to the cache, rather than the original {@code String}. The mime type
	 * cache WILL blow your memory if you don't.
	 */
	protected static final String JPEG = "image/jpeg";

	private static final Logger logger = ETLRegistry.getInstance()
			.getLogger(AbstractMimeTypeCache.class);

	private final SimpleHttpHead httpHead = new SimpleHttpHead();

	private final File cacheFile;
	private final int numEntries;

	private boolean changed = false;

	private int hits = 0;
	private int misses = 0;
	private int requestFailures = 0;

	AbstractMimeTypeCache(String cacheFileName)
	{
		File dir = DaoRegistry.getInstance().getConfiguration().getDirectory("medialib.data.dir");
		cacheFile = FileUtil.newFile(dir, cacheFileName);
		if (!cacheFile.isFile()) {
			String fmt = "Missing cache file (%s). You should put it in %s.";
			throw new ETLRuntimeException(String.format(fmt, cacheFileName, dir.getAbsolutePath()));
		}
		logger.info("Initializing mime type cache");
		numEntries = buildCache(cacheFile);
		logger.info(String.format("Initialization complete. Number of entries in cache: %s",
				numEntries));
	}

	@Override
	public void resetCounters()
	{
		hits = 0;
		misses = 0;
		requestFailures = 0;
	}

	/**
	 * Look up the mime type of the media file with the specified UnitID.
	 */
	public String getMimeType(String unitID)
	{
		String mimetype = getEntry(unitID);
		if (mimetype == null) {
			++misses;
			mimetype = JPEG;
			String fmt = "UnitID \"%s\" not found in mime type cache. The mime type cache is out-of-date!";
			logger.warn(String.format(fmt, unitID));
			//TODO: Re-enable medialib calls for mimetype lookups (???)
			//mimetype = callMedialib(unitID);
		}
		else {
			++hits;
		}
		return mimetype;
	}

	/**
	 * Returns the number of entries in the mime type cache.
	 */
	@Override
	public int getSize()
	{
		return numEntries;
	}

	/**
	 * Get the number of successful mime type lookups.
	 * 
	 * @return
	 */
	@Override
	public int getHits()
	{
		return hits;
	}

	/**
	 * Get the number failed cache lookups.
	 * 
	 * @return
	 */
	@Override
	public int getMisses()
	{
		return misses;
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
	 * Closes the cache. If the cache has changed since it was instantiated
	 * <i>and</i> a system property named "mimetypecache.update" exists and has
	 * value "true", the cache is saved back to the file system.
	 */
	@Override
	public void close() throws IOException
	{
		if (changed && ConfigObject.isEnabled("mimetypecache.update")) {
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

	@SuppressWarnings("unused")
	private String callMedialib(String unitID)
	{
		String mimetype;
		String fmt;
		String url = MEDIALIB_URL_START + unitID;
		mimetype = httpHead.setBaseUrl(url).execute().getHttpResponse()
				.getFirstHeader("Content-Type").getValue();
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
			fmt = "Error retrieving mime type for URL %s: HTTP %s (%s). Mime type set to \"\"";
			logger.warn(String.format(fmt, url, httpHead.getStatus(), httpHead.getError()));
		}
		return mimetype;
	}

}
