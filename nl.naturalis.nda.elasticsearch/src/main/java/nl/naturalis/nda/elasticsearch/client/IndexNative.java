package nl.naturalis.nda.elasticsearch.client;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.domainobject.util.ExceptionUtil;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.indices.IndexMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Wrapper around ElasticSearch's Native (Java) client. Since the API seems to
 * have some quirks, we may have to resort to the {@link IndexREST}, which
 * "simply" uses the REST API of ElasticSearch.
 * 
 * @author ayco_holleman
 * 
 */
public class IndexNative implements Index {

	private static final Logger logger = LoggerFactory.getLogger(IndexNative.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private static Client localClient;


	/**
	 * Returns {@code Client} configured using /src/main/resources/elasticsearch.yml
	 */
	public static final Client getDefaultClient()
	{
		if (localClient == null) {
			//localClient = nodeBuilder().client(true).data(false).node().client();
			localClient = nodeBuilder().node().client();
			localClient.admin().cluster().prepareHealth().setWaitForGreenStatus().execute().actionGet();
		}
		return localClient;
	}

	final Client esClient;
	final IndicesAdminClient admin;
	final String indexName;


	/**
	 * Create an instance manipulating the specified index using a default ES
	 * Java Client.
	 * 
	 * @param indexName The Lucene index that this instance will operate upon.
	 *            
	 * @see #getDefaultClient()
	 */
	public IndexNative(String indexName)
	{
		this.indexName = indexName;
		this.esClient = getDefaultClient();
		admin = esClient.admin().indices();
	}


	/**
	 * reate an instance manipulating the specified index using the specified
	 * client.
	 * 
	 * @param client The client
	 * @param indexName The index for which to create this instance. All
	 *            methods, except a few, will operate against this index.
	 */
	public IndexNative(Client client, String indexName)
	{
		this.indexName = indexName;
		this.esClient = client;
		admin = esClient.admin().indices();
	}

	/*
	 * Inner class only used to be thrown out of the action listener within the
	 * exists() method to signal that the index exists.
	 */
	@SuppressWarnings("serial")
	private class ExistsException extends RuntimeException {
	}


	public boolean exists()
	{
		logger.info(String.format("Verifying existence of index \"%s\"", indexName));
		IndicesExistsRequest request = new IndicesExistsRequest(indexName);
		try {
			admin.exists(request, new ActionListener<IndicesExistsResponse>() {
				public void onResponse(IndicesExistsResponse response)
				{
					if (response.isExists()) {
						throw new ExistsException();
					}
				}


				public void onFailure(Throwable e)
				{
					ExceptionUtil.smash(e);
				}
			});
		}
		catch (ExistsException e) {
			return true;
		}
		return false;
	}


	@Override
	public String describe()
	{
		GetMappingsRequest request = new GetMappingsRequest();
		request.indices(indexName);
		GetMappingsResponse response = admin.getMappings(request).actionGet();
		try {
			return objectMapper.writeValueAsString(response.getMappings());
		}
		catch (JsonProcessingException e) {
			throw ExceptionUtil.smash(e);
		}
	}


	@Override
	public String describeAllIndices()
	{
		// TODO Auto-generated method stub
		return null;
	}


	public void create()
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		CreateIndexResponse response = admin.create(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexException("Failed to create index " + indexName);
		}
		logger.info("Index created");
	}


	@Override
	public void create(String mappings)
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequest request = new CreateIndexRequest(indexName);
		request.mapping(indexName, mappings);
		CreateIndexResponse response = admin.create(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexException("Failed to create index " + indexName);
		}
		logger.info("Index created");
	}


	public void createType(String name, String mapping)
	{
		logger.info(String.format("Creating type \"%s\"", name));
		PutMappingRequest request = new PutMappingRequest(indexName);
		request.source(mapping);
		request.type(name);
		PutMappingResponse response = admin.putMapping(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexException(String.format("Failed to create type \"%s\"", name));
		}
	}


	/**
	 * Deletes the index for which this client was set up.
	 * 
	 * @return {@code true} if the index existed and was successfully deleted;
	 *         {@code false} if the index did not exist.
	 */
	public boolean delete()
	{
		logger.info("Deleting index " + indexName);
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		try {
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged()) {
				throw new IndexException("Failed to delete index " + indexName);
			}
			logger.info("Index deleted");
			return true;
		}
		catch (IndexMissingException e) {
			logger.info("No such index: " + indexName);
			return false;
		}
	}


	@Override
	public void deleteAllIndices()
	{
		logger.info("Deleting all indices in cluster");
		DeleteIndexRequest request = new DeleteIndexRequest("_all");
		try {
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged()) {
				throw new IndexException("Failed to delete index " + indexName);
			}
			logger.info("Indices deleted");
		}
		catch (Exception e) {
			logger.info("Failed to delete all indices in cluster: " + e.getMessage());
		}
	}


	@Override
	public void save(String type, String json, String id)
	{
		IndexRequest request = new IndexRequest(indexName, type, id);
		request.source(json);
		IndexResponse response = esClient.index(request).actionGet();
		//		IndexRequestBuilder irb = esClient.prepareIndex(indexName, type, id);
		//		irb.setSource(json);
		//		IndexResponse response = irb.execute().actionGet();
		if (!response.isCreated()) {
			throw new IndexException("Failed to add document: " + json);
		}
	}


	@Override
	public void saveObject(String type, Object obj, String id)
	{
		final String json;
		try {
			json = objectMapper.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new IndexException(e);
		}
		save(type, json, id);
	}


	public Client getClient()
	{
		return esClient;
	}

}
