package nl.naturalis.nba.etl;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface specifying the capacities of a mimetype cache for the medialib. A
 * mimetype cache maps UnitIDs to mimetypes. Implementations manage the chaching
 * and lookup of mime types to speed up multimedia imports.
 * 
 * @author Ayco Holleman
 *
 */
public interface MimeTypeCache extends Closeable {

	/**
	 * Defines the endpoint for medialib resource up to the ID part of the URL:
	 * {@code "http://medialib.naturalis.nl/file/id/"}.
	 */
	String MEDIALIB_URL_START = "https://medialib.naturalis.nl/file/id/";

  /**
   * Defines the legacy endpoint for medialib resource up to the ID part of the URL:
   * {@code "http://medialib.naturalis.nl/file/id/"}.
   */
  String MEDIALIB_HTTP_URL = "http://medialib.naturalis.nl/file/id/";
	
	 /**
   * Defines the endpoint for medialib resource up to the ID part of the URL:
   * {@code "https://medialib.naturalis.nl/file/id/"}.
   */
  String MEDIALIB_HTTPS_URL = "https://medialib.naturalis.nl/file/id/";

	
	/**
	 * Reset statistics counters (cache hits, medialib requests, request
	 * failures). Call this method if you are importing multiple data sources
	 * and you want a separate account for each of them separately.
	 */
	void resetCounters();

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
	String getMimeType(String unitID);

	/**
	 * Get the number of entries in the cache.
	 * 
	 * @return
	 */
	int getSize();

	/**
	 * Get the cache hit count, that is, the number of successful mimetype
	 * lookups (without having to call the medialib).
	 * 
	 * @return
	 */
	int getHits();

	/**
	 * Get the number of times the mimetype had to be retrieved by calling the
	 * medialib. In other words this methods doubles as a counter for the number
	 * of cache misses.
	 * 
	 * @return
	 */
	int getMisses();

	/**
	 * Get the number of times the request to the medialib for some reason
	 * failed to return HTTP 200 (OK)
	 * 
	 * @return
	 */
	int getRequestFailures();

	/**
	 * Saves and closes the cache. If the cache has changed since it was
	 * instantiated, it is saved back to the file system. When done, all
	 * resources held on to by the implementor class should be released. Clients
	 * you should always call this method when done using the cache. Otherwise,
	 * new cache entries for new media objects will not get saved to the file
	 * system, causing repetitive, expensive calls to the medialib.
	 */
	@Override
	void close() throws IOException;

}