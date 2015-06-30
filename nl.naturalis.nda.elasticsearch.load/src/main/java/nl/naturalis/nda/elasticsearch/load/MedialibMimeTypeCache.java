package nl.naturalis.nda.elasticsearch.load;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nda.elasticsearch.load.crs.CrsMimeTypeRetriever;
import nl.naturalis.nda.elasticsearch.load.crs.CrsMultiMediaTransfer;

import org.domainobject.util.http.SimpleHttpHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that manages the lookup and chaching of mime types of media
 * from the medialib. It is used on the fly by {@link CrsMultiMediaTransfer} to
 * determine the mime type of a media object, and by
 * {@link CrsMimeTypeRetriever} to pre-build a mime type cache. The cache maps
 * UnitIDs to mime types. In case of a cache miss (see
 * {@link #getMimeType(String)}), the cache sends an HTTP HEAD request to the
 * medialib to retrieve the mime type. Once you're done using the cache, you
 * should call it's {@link #close() close} method to save the cache to the local
 * file system. The next time the cache is instantiated it will initialize the
 * cache from the cache file. The cache file can be moved across servers to
 * expedite the import process on that server.
 * 
 * @author ayco_holleman
 *
 */
public class MedialibMimeTypeCache implements Closeable {

	public static final String MEDIALIB_URL_START = "http://medialib.naturalis.nl/file/id/";

	/*
	 * Directory containing files like nda-import.properties, logback.xml, etc.
	 */
	private static final String SYSPROP_CONFIG_DIR = "ndaConfDir";
	private static final Charset CACHE_FILE_CHARSET = Charset.forName("UTF-8");
	private static final byte[] NEWLINE_BYTES = "\n".getBytes(CACHE_FILE_CHARSET);
	private static final Logger logger = LoggerFactory.getLogger(MedialibMimeTypeCache.class);

	private static final String CACHE_FILE_NAME = "mimetypes.cache";
	private static final String CRS_CACHE_FILE_NAME = "crs-mimetypes.cache";
	private static final String BRAHMS_CACHE_FILE_NAME = "brahms-mimetypes.cache";
	private static final String NSR_CACHE_FILE_NAME = "nsr-mimetypes.cache";

	private static MedialibMimeTypeCache instance;
	private static MedialibMimeTypeCache crsInstance;
	private static MedialibMimeTypeCache brahmsInstance;
	private static MedialibMimeTypeCache nsrInstance;


	/**
	 * Get a generic mime type cache.
	 * 
	 * @return
	 */
	public static MedialibMimeTypeCache getInstance()
	{
		if (instance == null) {
			instance = new MedialibMimeTypeCache(CACHE_FILE_NAME);
		}
		return instance;
	}


	/**
	 * Get a mime type cache for CRS UnitIDs. Since the cache only contains
	 * UnitIDs from CRS, lookups are potentially faster
	 * 
	 * @return
	 */
	@Deprecated
	public static MedialibMimeTypeCache getCRSInstance()
	{
		if (crsInstance == null) {
			crsInstance = new MedialibMimeTypeCache(CRS_CACHE_FILE_NAME);
		}
		return crsInstance;
	}


	/**
	 * Get a mime type cache for Brahms UnitIDs. Since the cache only contains
	 * UnitIDs from Brahms, lookups are potentially faster
	 * 
	 * @return
	 */
	@Deprecated
	public static MedialibMimeTypeCache getBrahmsInstance()
	{
		if (brahmsInstance == null) {
			brahmsInstance = new MedialibMimeTypeCache(BRAHMS_CACHE_FILE_NAME);
		}
		return brahmsInstance;
	}


	/**
	 * Get a mime type cache for NSR UnitIDs. Since the cache only contains
	 * UnitIDs from NSR, lookups are potentially faster
	 * 
	 * @return
	 */
	@Deprecated
	public static MedialibMimeTypeCache getNSRInstance()
	{
		if (nsrInstance == null) {
			nsrInstance = new MedialibMimeTypeCache(NSR_CACHE_FILE_NAME);
		}
		return nsrInstance;
	}

	private final SimpleHttpHead httpHead = new SimpleHttpHead();

	private File cacheFile;
	private TreeMap<String, String> cache;
	private boolean changed;

	private int cacheHits = 0;
	private int medialibRequests = 0;
	private int requestFailures = 0;


	private MedialibMimeTypeCache(String cacheFileName)
	{
		String ndaConfDir = System.getProperty(SYSPROP_CONFIG_DIR);
		if (ndaConfDir == null) {
			throw new RuntimeException(String.format("Missing system property \"%1$s\". Add -D%1$s=/path/to/conf/dir to command line arguments",
					SYSPROP_CONFIG_DIR));
		}
		File dir = new File(ndaConfDir);
		if (!dir.isDirectory()) {
			throw new RuntimeException(String.format("Invalid directory specified for property \"ndaConfDir\": \"%s\"", ndaConfDir));
		}
		try {
			cacheFile = new File(dir.getAbsolutePath() + '/' + cacheFileName);
			if (!cacheFile.isFile()) {
				String fmt = "Missing cache file (%s). You should put it in %s. ";
				String msg = String.format(fmt, CACHE_FILE_NAME, ndaConfDir);
				throw new RuntimeException(msg);
			}
			buildCache();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Get the mime type for the media object with the specified UnitID. This
	 * method first checks an in-memory cache, instantiated from a file on the
	 * local file system. If the UnitID is not found in the cache, an HTTP HEAD
	 * request will be sent to the medialib to retrieve the mime type. Since it
	 * is an in-memory cache you should call {@link #close()} to flush the new
	 * cache entries back to the file system.
	 * 
	 * @param unitID
	 * @return
	 */
	public String getMimeType(String unitID)
	{
		String mimetype = cache.get(unitID);
		if (mimetype == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("Retrieving mime type for unitID \"%s\"", unitID));
			}
			String url = MEDIALIB_URL_START + unitID;
			++medialibRequests;
			mimetype = httpHead.setBaseUrl(url).execute().getHttpResponse().getFirstHeader("Content-Type").getValue();
			if (httpHead.isOK()) {
				cache.put(unitID, mimetype);
				changed = true;
			}
			else {
				++requestFailures;
				// We are still going to cache this UnitID, associating it
				// with an empty mime type, because it's probably not going
				// to get any better for this UnitID next time round, so no
				// use going out to the medialib again and again for this
				// UnitID.
				cache.put(unitID, "");
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


	/**
	 * Get the number of entries in the cache.
	 * 
	 * @return
	 */
	public int getSize()
	{
		return cache.size();
	}


	/**
	 * Get the number of times the request to the medialib for some reason
	 * failed to return HTTP 200 (OK)
	 * 
	 * @return
	 */
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
	public int getMedialibRequests()
	{
		return medialibRequests;
	}


	/**
	 * Forces the in-memory cache to be saved to the local file system, from
	 * where it will be loaded into memory again the next time the cache is
	 * instantiated. Saving the cache is also part of closing the cache (see
	 * {@link #close()}.
	 * 
	 * @throws IOException
	 */
	public void save() throws IOException
	{
		if (cacheFile.isFile()) {
			if (!cacheFile.delete()) {
				throw new IOException("Failed to delete " + cacheFile.getAbsolutePath());
			}
		}
		logger.info("Saving mime type cache to file system: " + cacheFile.getAbsolutePath());
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)));
			ZipEntry zipEntry = new ZipEntry("mimetypes");
			zos.putNextEntry(zipEntry);
			for (Map.Entry<String, String> entry : cache.entrySet()) {
				zos.write(entry.getKey().getBytes(CACHE_FILE_CHARSET));
				zos.write(NEWLINE_BYTES);
				zos.write(entry.getValue().getBytes(CACHE_FILE_CHARSET));
				zos.write(NEWLINE_BYTES);
			}
		}
		finally {
			zos.close();
		}
	}


	/**
	 * Closes the cache. Notably, if the cache has changed since it was
	 * instantiated, it is saved back to the file system. Therefore you should
	 * always call this method once you're done using the cache. Otherwise, new
	 * cache entries for new media objects will not get saved to the file
	 * system, causing repeated, expensive calls to the medialib.
	 */
	public void close() throws IOException
	{
		httpHead.shutdown();
		if (changed) {
			save();
		}
	}


	/*
	 * Build cache from file system file, if it exists.
	 */
	private void buildCache() throws FileNotFoundException, IOException
	{
		cache = new TreeMap<String, String>();
		logger.info("Initializing mime type cache");
		LineNumberReader lnr = null;
		ZipInputStream zis = null;
		try {
			zis = new ZipInputStream(new FileInputStream(cacheFile));
			zis.getNextEntry();
			InputStreamReader isr = new InputStreamReader(zis, CACHE_FILE_CHARSET);
			lnr = new LineNumberReader(isr, 1024 * 1024);
			String key;
			String mimeType;
			while ((key = lnr.readLine()) != null) {
				mimeType = lnr.readLine();
				if (mimeType == null) {
					throw new RuntimeException("Unexpected end of cache file");
				}
				cache.put(key, mimeType);
			}
			logger.info(String.format("Mime type cache ready (%s entries)", cache.size()));
		}
		finally {
			if (lnr != null) {
				lnr.close();
			}
			else if (zis != null) {
				zis.close();
			}
		}
	}

}
