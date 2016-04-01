package nl.naturalis.nba.etl.elasticsearch;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.MIMETYPE_JSON;

import java.util.Collection;
import java.util.List;

import nl.naturalis.nba.etl.Registry;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.http.SimpleHttpDelete;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpPut;
import org.domainobject.util.http.SimpleHttpRequest;

/**
 * Implementation of {@link IndexManager} that uses ElasticSearch's REST API to
 * interact with an index. This implementation is no longer used and is no
 * longer up-to-date.
 * 
 * @author Ayco Holleman
 * 
 */
@Deprecated
public class IndexManagerREST implements IndexManager {

	/**
	 * The default URL through which to access ElasticSearch
	 * (http://localhost:9200).
	 */
	public static final String LOCAL_CLUSTER = "http://localhost:9200/";

	private static final Logger logger = Registry.getInstance().getLogger(IndexManagerREST.class);

	private final SimpleHttpGet httpGet = new SimpleHttpGet();
	private final SimpleHttpPut httpPut = new SimpleHttpPut();
	private final SimpleHttpDelete httpDelete = new SimpleHttpDelete();
	private final String indexName;

	private SimpleHttpRequest lastRequest;

	/**
	 * Creates an instance manipulating the specified index on the local cluster
	 * at {@link #LOCAL_CLUSTER}.
	 * 
	 * @param indexName
	 *            The index for which to create this instance. All methods,
	 *            except a few, will operate against this index.
	 */
	public IndexManagerREST(String indexName)
	{
		this(indexName, LOCAL_CLUSTER);
	}

	/**
	 * Create an instance representing specified index, accessed through the
	 * specified cluster URL.
	 * 
	 * @param indexName
	 *            The index for which to create this instance. All methods,
	 *            except a few, will operate against this index.
	 * @param clusterUrl
	 *            The cluster URL
	 */
	public IndexManagerREST(String indexName, String clusterUrl)
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
		if (httpGet.getStatus() == HTTP_NOT_FOUND) {
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
	 * @see
	 * nl.naturalis.nda.elasticsearch.client.Index#createType(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void addType(String name, String mapping)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName + "/" + name + "/_mapping");
		httpPut.setRequestBody(mapping);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}

	@Override
	public boolean deleteType(String name)
	{
		return false;
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
		if (httpDelete.getStatus() == HTTP_NOT_FOUND) {
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

	@Override
	public void saveDocument(String type, String json, String id)
	{
		lastRequest = httpPut;
		StringBuilder sb = new StringBuilder(32).append(indexName).append('/').append(type).append('/').append(id);
		httpPut.setPath(sb.toString());
		httpPut.setRequestBody(json);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}

	@Override
	public void saveObject(String type, Object obj, String id)
	{
		lastRequest = httpPut;
		StringBuilder sb = new StringBuilder(32).append(indexName).append('/').append(type).append('/').append(id);
		httpPut.setPath(sb.toString());
		httpPut.setContentType(MIMETYPE_JSON);
		httpPut.setObject(obj);
		execute();
		if (!isSuccess()) {
			throw createIndexException();
		}
	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids)
	{
		// TODO Auto-generated method stub

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
		// TODO
		return null;
	}

	/**
	 * Get the HTTP status code for the most recently executed request
	 * 
	 * @return The HTTP status code
	 */
	public int getStatus()
	{
		return lastRequest.getStatus();
	}

	/**
	 * Convenience method indicating whether or not the most recently executed
	 * resulted in a HTTP success status (&gt;= 200 and &lt; 300).
	 * 
	 * @return Whether the most recently executed request complete successfully
	 */
	public boolean isSuccess()
	{
		return lastRequest.getStatus() >= 200 && lastRequest.getStatus() < 300;
	}

	private void execute()
	{
		//logger.debug(lastRequest.getMethod() + " " + lastRequest.getURL());
		try {
			lastRequest.execute();
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
	}

	private IndexManagerException createIndexException()
	{
		String fmt = "Error while executing request: code=\"%s\";message=\"%s\"";
		String msg = String.format(fmt, lastRequest.getStatus(), lastRequest.getError());
		return new IndexManagerException(msg);
	}

	@Override
	public void saveObjects(String type, List<?> objs)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids, List<String> parentIds)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void saveObject(String type, Object obj, String id, String parentId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T get(String type, String id, Class<T> targetClass)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteDocument(String type, String id)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean typeExists(String type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteWhere(String type, String field, String value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void create(int numShards, int numReplicas)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void create(String settings)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> get(String type, Collection<String> ids, Class<T> targetClass)
	{
		return null;
	}

}