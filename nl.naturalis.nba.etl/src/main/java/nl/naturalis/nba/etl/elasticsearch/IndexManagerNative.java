package nl.naturalis.nba.etl.elasticsearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ExceptionUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.IndexNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.etl.Registry;

/**
 * An implementation of {@link IndexManager} that uses ElasticSearch's Native
 * Java client. This is the implementation of the {@link IndexManager} interface
 * used throughout the NBA Import library.
 * 
 * @author Ayco Holleman
 * 
 */
public class IndexManagerNative implements IndexManager {

	private static final Logger logger = Registry.getInstance().getLogger(IndexManagerNative.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	private final Client esClient;
	private final IndicesAdminClient admin;
	private final String indexName;

	/**
	 * Create an instance manipulating the specified index using the specified
	 * client.
	 * 
	 * @param client
	 *            The client
	 * @param indexName
	 *            The index for which to create this instance. All methods,
	 *            except a few, will operate against this index.
	 */
	public IndexManagerNative(Client client, String indexName)
	{
		this.indexName = indexName;
		this.esClient = client;
		admin = esClient.admin().indices();
	}

	@Override
	public boolean exists()
	{
		logger.info(String.format("Verifying existence of index \"%s\"", indexName));
		IndicesExistsRequestBuilder irb = admin.prepareExists();
		irb.setIndices(indexName);
		IndicesExistsResponse response = irb.execute().actionGet();
		return response.isExists();
	}

	@Override
	public boolean typeExists(String type)
	{
		logger.info(String.format("Verifying existence of type \"%s\"", type));
		TypesExistsRequestBuilder terb = admin.prepareTypesExists();
		terb.setIndices(new String[] { indexName });
		terb.setTypes(type);
		TypesExistsResponse response = terb.execute().actionGet();
		return response.isExists();
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

	@Override
	public void create()
	{
		create(1, 0);
	}

	@Override
	public void create(int numShards, int numReplicas)
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequestBuilder request = admin.prepareCreate(indexName);
		HashMap<String, Object> settings = new HashMap<>();
		settings.put("number_of_shards", numShards);
		settings.put("number_of_replicas", numReplicas);
		request.setSettings(settings);
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexManagerException("Failed to create index " + indexName);
		}
		logger.info("Index created");
	}

	@Override
	public void create(String settings)
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequestBuilder request = admin.prepareCreate(indexName);
		Builder builder = Settings.settingsBuilder();
		builder.loadFromSource(settings);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexManagerException("Failed to create index " + indexName);
		}
		logger.info("Index created");
	}

	@Override
	public boolean delete()
	{
		logger.info("Deleting index " + indexName);
		DeleteIndexRequest request = new DeleteIndexRequest(indexName);
		try {
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged()) {
				throw new IndexManagerException("Failed to delete index " + indexName);
			}
			logger.info("Index deleted");
			return true;
		}
		catch (IndexNotFoundException e) {
			logger.info(String.format("No such index \"%s\" (nothing deleted)", indexName));
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
				throw new IndexManagerException("Failed to delete index " + indexName);
			}
			logger.info("Indices deleted");
		}
		catch (Exception e) {
			logger.info("Failed to delete all indices in cluster: " + e.getMessage());
		}
	}

	@Override
	public void addType(String name, String mapping)
	{
		logger.info(String.format("Creating type \"%s\"", name));
		PutMappingRequest request = new PutMappingRequest(indexName);
		request.source(mapping);
		request.type(name);
		PutMappingResponse response = admin.putMapping(request).actionGet();
		if (!response.isAcknowledged()) {
			throw new IndexManagerException(String.format("Failed to create type \"%s\"", name));
		}
	}

	@Override
	public boolean deleteType(String name)
	{
//		logger.info(String.format("Deleting type \"%s\"", name));
//		DeleteMappingRequestBuilder request = esClient.admin().indices().prepareDeleteMapping();
//		request.setIndices(indexName);
//		request.setType(name);
//		try {
//			DeleteMappingResponse response = request.execute().actionGet();
//			if (!response.isAcknowledged()) {
//				throw new IndexManagerException(String.format("Failed to delete type \"%s\"", name));
//			}
//			logger.info("Type deleted");
//			return true;
//		}
//		catch (TypeMissingException e) {
//			logger.info(String.format("No such type \"%s\" (nothing deleted)", name));
//			return false;
//		}
		return true;
	}

	@Override
	public <T> T get(String type, String id, Class<T> targetClass)
	{
		GetRequestBuilder grb = esClient.prepareGet();
		grb.setIndex(indexName);
		grb.setType(type);
		grb.setId(id);
		GetResponse response = grb.execute().actionGet();
		if (response.isExists()) {
			try {
				return objectMapper.readValue(response.getSourceAsBytes(), targetClass);
			}
			catch (Exception e) {
				throw new IndexManagerException(e);
			}
		}
		return null;
	}

	@Override
	public <T> List<T> get(String type, Collection<String> ids, Class<T> targetClass)
	{
		MultiGetRequestBuilder mgrb = esClient.prepareMultiGet();
		mgrb.add(indexName, type, ids);
		MultiGetResponse response = mgrb.execute().actionGet();
		ArrayList<T> result = new ArrayList<>(ids.size());
		try {
			for (MultiGetItemResponse item : response) {
				if (!item.isFailed()) {
					byte[] bytes = item.getResponse().getSourceAsBytes();
					if (bytes != null) {
						result.add(objectMapper.readValue(bytes, targetClass));
					}
				}
				else {
					logger.error(item.getFailure().getMessage());
				}
			}
		}
		catch (IOException e) {
			throw new IndexManagerException(e);
		}
		return result;
	}

	@Override
	public boolean deleteDocument(String type, String id)
	{
		DeleteRequestBuilder drb = esClient.prepareDelete();
		drb.setId(id);
		drb.setType(type);
		drb.setIndex(indexName);
		DeleteResponse response = drb.execute().actionGet();
		return response.isFound();
	}

	@Override
	public void deleteWhere(String type, String field, String value)
	{
//		logger.info(String.format("Deleting %s documents where %s equals \"%s\"", type, field, value));
//		DeleteByQueryRequestBuilder request = esClient.prepareDeleteByQuery();
//		request.setTypes(type);
//		TermFilterBuilder filter = FilterBuilders.termFilter(field, value);
//		FilteredQueryBuilder query = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter);
//		request.setQuery(query);
//		request.execute().actionGet();
	}

	@Override
	public void saveDocument(String type, String json, String id)
	{
		IndexRequestBuilder irb = esClient.prepareIndex(indexName, type, id);
		irb.setSource(json);
		irb.execute().actionGet();
	}

	@Override
	public void saveObject(String type, Object obj, String id)
	{
		saveObject(type, obj, id, null);
	}

	@Override
	public void saveObject(String type, Object obj, String id, String parentId)
	{
		final String json;
		try {
			json = objectMapper.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new IndexManagerException(e);
		}
		IndexRequestBuilder irb = esClient.prepareIndex(indexName, type);
		if (id != null) {
			irb.setId(id);
		}
		if (parentId != null) {
			irb.setParent(parentId);
		}
		irb.setSource(json);
		irb.execute().actionGet();
	}

	@Override
	public void saveObjects(String type, List<?> objs) throws BulkIndexException
	{
		saveObjects(type, objs, null, null);
	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids) throws BulkIndexException
	{
		saveObjects(type, objs, ids, null);
	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids, List<String> parentIds) throws BulkIndexException
	{
		BulkRequestBuilder brb = esClient.prepareBulk();
		for (int i = 0; i < objs.size(); ++i) {
			IndexRequestBuilder irb = esClient.prepareIndex(indexName, type);
			try {
				irb.setSource(objectMapper.writeValueAsBytes(objs.get(i)));
				if (ids != null)
					irb.setId(ids.get(i));
				if (parentIds != null)
					irb.setParent(parentIds.get(i));
			}
			catch (JsonProcessingException e) {
				throw new IndexManagerException(e);
			}
			brb.add(irb);
		}
		BulkResponse response = brb.execute().actionGet();
		if (response.hasFailures())
			throw new BulkIndexException(response, objs);
	}

	public Client getClient()
	{
		return esClient;
	}

}
