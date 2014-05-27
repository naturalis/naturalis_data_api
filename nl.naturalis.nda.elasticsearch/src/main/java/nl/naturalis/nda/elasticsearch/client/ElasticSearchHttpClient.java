package nl.naturalis.nda.elasticsearch.client;

import org.domainobject.util.ExceptionUtil;
import org.domainobject.util.http.SimpleHttpDelete;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpPut;
import org.domainobject.util.http.SimpleHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElasticSearchHttpClient {

	public static void main(String[] args)
	{
		ElasticSearchHttpClient sesc = new ElasticSearchHttpClient("nda");
		sesc.createIndex();
		sesc.describeAllIndices();
		sesc.deleteAllIndices();
		sesc.describeAllIndices();
	}

	private static final Logger logger = LoggerFactory.getLogger(ElasticSearchHttpClient.class);
	private static final String LOCAL_CLUSTER = "http://localhost:9200/";

	@SuppressWarnings("serial")
	public static class Exception extends RuntimeException {
		public Exception(String message)
		{
			super(message);
		}
	}

	private final SimpleHttpGet httpGet = new SimpleHttpGet();
	private final SimpleHttpPut httpPut = new SimpleHttpPut();
	private final SimpleHttpDelete httpDelete = new SimpleHttpDelete();
	private final String indexName;

	private SimpleHttpRequest lastRequest;


	/**
	 * Creates an {@code ElasticSearchHttpClient} for the specified index and
	 * for the local cluster at http://localhost:9200/
	 * 
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 */
	public ElasticSearchHttpClient(String indexName)
	{
		this(indexName, LOCAL_CLUSTER);
	}


	/**
	 * Creates an {@code ElasticSearchHttpClient} for the specified index and
	 * the specified cluster URL.
	 * 
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 * @param clusterUrl The cluster URL
	 */
	public ElasticSearchHttpClient(String indexName, String clusterUrl)
	{
		this.indexName = indexName;
		httpGet.setBaseUrl(clusterUrl);
		httpPut.setBaseUrl(clusterUrl);
		httpDelete.setBaseUrl(clusterUrl);
	}


	/**
	 * Gets the HTTP status code for the most recently executed request.
	 * 
	 * @return The HTTP status code
	 */
	public int getErrorCode()
	{
		return lastRequest.getErrorCode();
	}


	/**
	 * Adds a new document to the index, or overrides if the _id already exists.
	 * 
	 * @param type The type of the document
	 * @param obj An object represting the document. The document will be
	 *            coverted to JSON and then added to the index.
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String addDocument(String type, Object obj)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName + "/" + type);
		httpPut.objectToRequestBody(obj);
		return execute(httpPut);
	}


	/**
	 * Describe the index (i.e. its mapping).
	 * 
	 * @return The mapping
	 */
	public String describe()
	{
		lastRequest = httpGet;
		httpGet.setPath(indexName + "/_mapping?pretty");
		return execute(httpGet);
	}


	/**
	 * Describes all indices (i.e. their mappings) in the cluster.
	 * 
	 * @return The mappings
	 */
	public String describeAllIndices()
	{
		lastRequest = httpGet;
		httpGet.setPath("_aliases?pretty");
		return execute(httpGet);
	}


	/**
	 * Create the index.
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String createIndex()
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName);
		return execute(httpPut);
	}


	/**
	 * Create the index along with the specified mappings
	 * 
	 * @param mappings The mappings to create in the index.
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String createIndex(String mappings)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName);
		httpPut.setRequestBody(mappings);
		return execute(httpPut);
	}


	/**
	 * Adds a new type to the index, or overrides an existing one.
	 * 
	 * @param typeName The name of the type
	 * @param mapping The mapping for the type
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String addType(String typeName, String mapping)
	{
		lastRequest = httpPut;
		httpPut.setPath(indexName + "/" + typeName + "/_mapping");
		httpPut.setRequestBody(mapping);
		return execute(httpPut);
	}


	/**
	 * Deletes the index from the cluster.
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String deleteIndex()
	{
		lastRequest = httpDelete;
		httpDelete.setPath(indexName);
		return execute(httpDelete);
	}


	/**
	 * deletes all indices from the cluster (not necessarily cool).
	 * 
	 * @return The response message from ElasticSearch
	 */
	public String deleteAllIndices()
	{
		lastRequest = httpDelete;
		httpDelete.setPath("_all");
		return execute(httpDelete);
	}


	private static String execute(SimpleHttpRequest request)
	{
		logger.info(request.getMethod() + " " + request.getURL());
		try {
			request.execute();
		}
		catch (Throwable t) {
			throw ExceptionUtil.smash(t);
		}
		if (request.isOK()) {
			return request.getResponse();
		}
		String msg = String.format("Error while executing request: code=\"%s\";message=\"%s\"", request.getErrorCode(), request.getError());
		throw new ElasticSearchHttpClient.Exception(msg);
	}

}
