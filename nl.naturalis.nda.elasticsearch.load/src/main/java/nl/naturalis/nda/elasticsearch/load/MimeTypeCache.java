package nl.naturalis.nda.elasticsearch.load;

import java.io.Closeable;
import java.io.IOException;

/**
 * An interface specifying the capacities of a mime type cache for the medialib.
 * Implementations manage the lookup and chaching of mime types of media from
 * the medialib to speed up imports of multimedia documents.
 * 
 * @author ayco_holleman
 *
 */
public interface MimeTypeCache extends Closeable {

	/**
	 * Defines the endpoint for medialib resource up to the ID part of the URL:
	 * {@code "http://medialib.naturalis.nl/file/id/"}.
	 */
	public static final String MEDIALIB_URL_START = "http://medialib.naturalis.nl/file/id/";


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
	 * Get the <i>actual</i> number of entries in the cache.
	 * 
	 * @return
	 */
	int getSize();


	/**
	 * Get the number of times the request to the medialib for some reason
	 * failed to return HTTP 200 (OK)
	 * 
	 * @return
	 */
	int getRequestFailures();


	/**
	 * Get the number of successful mime type lookups (without having to call
	 * the medialib).
	 * 
	 * @return
	 */
	int getCacheHits();


	/**
	 * Get the number of times the mime type had to be retrieved by calling the
	 * medialib.
	 * 
	 * @return
	 */
	int getMedialibRequests();


	/**
	 * Saves and closes the cache. If the cache has changed since it was
	 * instantiated, it is saved back to the file system. When done, all
	 * resources held on to by the implementor class should be released. Clients
	 * you should always call this method when done using the cache. Otherwise,
	 * new cache entries for new media objects will not get saved to the file
	 * system, causing repetitivem expensive calls to the medialib.
	 */
	@Override
	void close() throws IOException;

}