package nl.naturalis.nda.elasticsearch.client;

import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.http.SimpleHttpDelete;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpPut;
import org.domainobject.util.http.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link Index} that uses ElasticSearch's REST interface to
 * manipulate an index. Not ideal, but currently more trustworthy than the
 * native API. Most methods will throw an {@link IndexException} if something
 * went wrong. This is a {@link RuntimeException}, but you can, of course, still
 * catch it.
 * 
 * @author ayco_holleman
 * 
 */
public class IndexREST implements Index {

	/**
	 * The default URL through which to access ElasticSearch
	 * (http://localhost:9200).
	 */
	public static final String LOCAL_CLUSTER = "http://localhost:9200/";

	private static final Logger logger = LoggerFactory.getLogger(IndexREST.class);

	private final SimpleHttpGet httpGet = new SimpleHttpGet();
	private final SimpleHttpPut httpPut = new SimpleHttpPut();
	private final SimpleHttpDelete httpDelete = new SimpleHttpDelete();
	private final String indexName;

	private SimpleHttpRequest lastRequest;


	/**
	 * Creates an instance manipulating the specified index on the local cluster
	 * at {@link #LOCAL_CLUSTER}.
	 * 
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 */
	public IndexREST(String indexName)
	{
		this(indexName, LOCAL_CLUSTER);
	}


	/**
	 * Create an instance representing specified index, accessed through the
	 * specified cluster URL.
	 * 
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 * @param clusterUrl The cluster URL
	 */
	public IndexREST(String indexName, String clusterUrl)
	{
		this.indexName = indexName;
		httpGet.setBaseUrl(clusterUrl);
		httpPut.setBaseUrl(clusterUrl);
		httpDelete.setBaseUrl(clusterUrl);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#exists()
	 */
	@Override
	public boolean exists()
	{
		return false;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#describe()
	 */
	@Override
	public String describe()
	{
		lastRequest = httpGet;
		httpGet.setPath(indexName + "/_mapping?pretty");
		execute();
		if (isSuccess()) {
			return getResponse();
		}
		if (httpGet.getStatus() == SimpleHttpRequest.HTTP_NOT_FOUND) {
			return null;
		}
		throw createIndexException();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#describeAllIndices()
	 */
	@Override
	public String describeAllIndices()
	{
		lastRequest = httpGet;
		httpGet.setPath("_aliases?pretty");
		execute();
		if (isSuccess()) {
			return getResponse();
		}
		throw createIndexException();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#create()
	 */
	@Override
	public void create()
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#create(java.lang.String)
	 */
	@Override
	public void create(String mappings)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName);
		httpPut.setRequestBody(mappings);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.naturalis.nda.elasticsearch.client.Index#createType(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void createType(String name, String mapping)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName + "/" + name + "/_mapping");
		httpPut.setRequestBody(mapping);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#delete()
	 */
	@Override
	public boolean delete()
	{
		lastRequest = httpDelete;
		httpDelete.setPath(indexName);
		execute();
		if (isSuccess()) {
			logger.info("Index " + indexName + " deleted");
			return true;
		}
		if (httpDelete.getStatus() == SimpleHttpRequest.HTTP_NOT_FOUND) {
			logger.info("Index " + indexName + " does not exist (nothing deleted)");
			return false;
		}
		throw createIndexException();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see nl.naturalis.nda.elasticsearch.client.Index#deleteAllIndices()
	 */
	@Override
	public void deleteAllIndices()
	{
		lastRequest = httpDelete;
		httpDelete.setPath("_all");
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nl.naturalis.nda.elasticsearch.client.Index#addDocument(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void addDocument(String type, Object obj)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName + "/" + type);
		httpPut.objectToRequestBody(obj);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}


	/**
	 * Get the response for the most recently executed request. Useful if you
	 * want to print or log status messages for requests that just do something
	 * without returning a response body (e.g. delete the index).
	 * 
	 * @return The response for the most recently executed request
	 */
	public String getResponse()
	{
		return lastRequest.getResponse();
	}


	/**
	 * Convenience method indicating whether the most recently executed request
	 * completed successfully (i.e. resulting in HTTP 200, OK).
	 * 
	 * @return Whether the most recently executed request complete successfully
	 */
	public boolean isSuccess()
	{
		return lastRequest.getStatus() == SimpleHttpRequest.HTTP_OK;
	}


	/**
	 * Convenience method indicating whether the most recently executed request
	 * resulted in HTTP status 404, NOT FOUND.
	 * 
	 * @return Whether the most recently executed request resulted in HTTP
	 *         status 404, NOT FOUND
	 */
	public boolean isNotFound()
	{
		return lastRequest.getStatus() == SimpleHttpRequest.HTTP_NOT_FOUND;
	}


	/**
	 * Gets the HTTP status code for the most recently executed request.
	 * 
	 * @return The HTTP status code
	 */
	public int getHttpStatusCode()
	{
		return lastRequest.getStatus();
	}


	private void execute()
	{
		logger.info(lastRequest.getMethod() + " " + lastRequest.getURL());
		try {
			lastRequest.execute();
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
		if (!lastRequest.isOK()) {
			throw createIndexException();
		}
	}


	private IndexException createIndexException()
	{
		String fmt = "Error while executing request: code=\"%s\";message=\"%s\"";
		String msg = String.format(fmt, lastRequest.getStatus(), lastRequest.getError());
		return new IndexException(msg);
	}

}
