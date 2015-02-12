package nl.naturalis.nda.elasticsearch.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;

import org.domainobject.util.ExceptionUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.indices.TypeMissingException;
import org.elasticsearch.search.SearchHit;
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
public class IndexNative implements Index
{

	private static final Logger logger = LoggerFactory.getLogger(IndexNative.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	final Client esClient;
	final IndicesAdminClient admin;
	final String indexName;

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
	public IndexNative(Client client, String indexName)
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
		terb.setIndices(new String[]
		{ indexName });
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
		try
		{
			return objectMapper.writeValueAsString(response.getMappings());
		} catch (JsonProcessingException e)
		{
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
		HashMap<String, Object> settings = new HashMap<String, Object>();
		settings.put("number_of_shards", numShards);
		settings.put("number_of_replicas", numReplicas);
		request.setSettings(settings);
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged())
		{
			throw new IndexException("Failed to create index " + indexName);
		}
		logger.info("Index created");
	}

	@Override
	public void create(String settings)
	{
		logger.info("Creating index " + indexName);
		CreateIndexRequestBuilder request = admin.prepareCreate(indexName);
		request.setSettings(ImmutableSettings.settingsBuilder().loadFromSource(settings).build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged())
		{
			throw new IndexException("Failed to create index " + indexName);
		}
		logger.info("Index created");
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
		try
		{
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged())
			{
				throw new IndexException("Failed to delete index " + indexName);
			}
			logger.info("Index deleted");
			return true;
		} catch (IndexMissingException e)
		{
			logger.info(String.format("No such index \"%s\" (nothing deleted)", indexName));
			return false;
		}
	}

	@Override
	public void deleteAllIndices()
	{
		logger.info("Deleting all indices in cluster");
		DeleteIndexRequest request = new DeleteIndexRequest("_all");
		try
		{
			DeleteIndexResponse response = admin.delete(request).actionGet();
			if (!response.isAcknowledged())
			{
				throw new IndexException("Failed to delete index " + indexName);
			}
			logger.info("Indices deleted");
		} catch (Exception e)
		{
			logger.info("Failed to delete all indices in cluster: " + e.getMessage());
		}
	}

	public void addType(String name, String mapping)
	{
		logger.info(String.format("Creating type \"%s\"", name));
		PutMappingRequest request = new PutMappingRequest(indexName);
		request.source(mapping);
		request.type(name);
		PutMappingResponse response = admin.putMapping(request).actionGet();
		if (!response.isAcknowledged())
		{
			throw new IndexException(String.format("Failed to create type \"%s\"", name));
		}
	}

	@Override
	public boolean deleteType(String name)
	{
		logger.info(String.format("Deleting type \"%s\"", name));
		DeleteMappingRequestBuilder request = esClient.admin().indices().prepareDeleteMapping();
		request.setIndices(indexName);
		request.setType(name);
		try
		{
			DeleteMappingResponse response = request.execute().actionGet();
			if (!response.isAcknowledged())
			{
				throw new IndexException(String.format("Failed to delete type \"%s\"", name));
			}
			logger.info("Type deleted");
			return true;
		} catch (TypeMissingException e)
		{
			logger.info(String.format("No such type \"%s\" (nothing deleted)", name));
			return false;
		}
	}

	public <T> T get(String type, String id, Class<T> targetClass)
	{
		GetRequestBuilder grb = esClient.prepareGet();
		grb.setIndex(indexName);
		grb.setType(type);
		grb.setId(id);
		GetResponse response = grb.execute().actionGet();
		if (response.isExists())
		{
			try
			{
				return objectMapper.readValue(response.getSourceAsString(), targetClass);
			} catch (Exception e)
			{
				throw new IndexException(e);
			}
		}
		return null;
	}

	/*
	 * Create by Reinier Description: for the DwCA export Date: 29-01-2015
	 */
	public <T> T getAll(String type, Class<T> targetClass)
	{
		GetRequestBuilder reqbld = esClient.prepareGet();

		/* Sets the index of the document to fetch. */
		reqbld.setIndex(indexName);

		/* Set the type of the document to fetch */
		reqbld.setType(type);

		/* Execute the Get Request builder */
		GetResponse resp = reqbld.execute().actionGet();

		/* if result exists from the response */
		if (resp.isExists())
		{
			try
			{
				/* read from the file and convert it to the targetClass */
				return objectMapper.readValue(resp.getSourceAsString(), targetClass);
			} catch (Exception e)
			{
				throw new IndexException(e);
			}
		}
		return null;
	}

	/*
	 * Created by Reinier Description: for the DwCA export Date: 29-01-2015
	 */
//	public HashMap<String, Object> getResultsMap(String type, int size)
//	{
//		ArrayList<Map<String, Object>> list = getResultsList(type, size, null);
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("type", type);
//		map.put("results", list);
//		Set<?> set = map.entrySet();
//		Iterator<?> i = set.iterator();
//		while(i.hasNext())
//		{
//			@SuppressWarnings("rawtypes")
//			Map.Entry me = (Map.Entry)i.next();
//			System.out.print(me.getKey() + ": ");
//			System.out.println(me.getValue());
//		}
//		
//		try 
//		{
//			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("output.txt")));
//			for(int t=0; t <list.size(); t++)
//			{
//				dos.writeBytes(list.get(t).toString() + System.lineSeparator());
//			}
//				dos.close();
//				System.out.println("Data saved.");
//			} 
//		    catch (IOException e) 
//		    {
//		    	e.printStackTrace();
//			} 		
//		
//		return map;
//	}
//	
	

	public <T> List<T> getResultsList(String type, int size,Class<T> targetClass)
	{
		SearchRequestBuilder searchRequestBuilder = esClient.prepareSearch()
				.setQuery(QueryBuilders.matchAllQuery())
				.setSearchType(SearchType.SCAN)
				.setScroll(new TimeValue(60000))
				.setIndices(indexName)
				.setTypes(type)
				.setSize(size);

		SearchResponse response = searchRequestBuilder.execute().actionGet();

		System.out.println("Scrollid:" + response.getScrollId());

		long totalHitCount = response.getHits().getTotalHits();
		System.out.println("Total hits: " + totalHitCount);

		/* Show response properties */
		String output = response.toString();
		System.out.println(output);

		List<T> list = new ArrayList<T>();
        
		while (true)
		{
			response = esClient.prepareSearchScroll(response.getScrollId())
					.setScrollId(response.getScrollId())
					.setScroll(new TimeValue(600000))
					.execute().actionGet();

			try
			{
				for (SearchHit hit : response.getHits())
				{
					
					T result = objectMapper.convertValue(hit.getSource(), targetClass);
					list.add(result);
					
					//Break condition: No hits are returned
				    if (response.getHits().hits().length == 0) 
				    {
				    	logger.info("No hits");
				        break;
				    }
					
					// the retrieved document
//					Map<String, Object> result = hit.getSource();
//					result.putAll(result);
//					
//					System.out.println(hit.getSource());
//					System.out.println(hit.getSourceAsString());

									
					//getting an iterator object to browse list items
//					nameOfList.add(hit.getSourceAsString().toString() + System.lineSeparator());
//					Iterator<Object> itr= nameOfList.iterator();
//
//					while(itr.hasNext())
//					{
//						resultOfList.add(nameOfList); 
//						System.out.println(itr.next());
//					}
//					

				}
				{
//          	    ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("SpecimenObjects.txt"));
//				/* Write the resultOfList object to the objectoutputstream. 
//				 * This writes the object as well all objects that it referes to.
//				   It writes only those objects that implement serializable
//			    */
//			    objectOutputStream.writeObject(list);
//				objectOutputStream.flush();
//				objectOutputStream.close();
				}
			} catch (Exception e)
			{
				throw new IndexException(e);
			}
			
    		return list;
		}
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
		logger.info(String.format("Deleting %s documents where %s equals \"%s\"", type, field, value));
		DeleteByQueryRequestBuilder request = esClient.prepareDeleteByQuery();
		request.setTypes(type);
		TermFilterBuilder filter = FilterBuilders.termFilter(field, value);
		FilteredQueryBuilder query = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), filter);
		request.setQuery(query);
		request.execute().actionGet();
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
		try
		{
			json = objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException e)
		{
			throw new IndexException(e);
		}
		IndexRequestBuilder irb = esClient.prepareIndex(indexName, type);
		if (id != null)
		{
			irb.setId(id);
		}
		if (parentId != null)
		{
			irb.setParent(parentId);
		}
		irb.setSource(json);
		irb.execute().actionGet();
	}

	@Override
	public void saveObjects(String type, List<?> objs)
	{
		saveObjects(type, objs, null, null);
	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids)
	{
		saveObjects(type, objs, ids, null);
	}

	@Override
	public void saveObjects(String type, List<?> objs, List<String> ids, List<String> parentIds)
	{
		BulkRequestBuilder brb = esClient.prepareBulk();
		for (int i = 0; i < objs.size(); ++i)
		{
			IndexRequestBuilder irb = esClient.prepareIndex(indexName, type);
			try
			{
				irb.setSource(objectMapper.writeValueAsString(objs.get(i)));
				if (ids != null)
				{
					irb.setId(ids.get(i));
				}
				if (parentIds != null)
				{
					irb.setParent(parentIds.get(i));
				}
			} catch (JsonProcessingException e)
			{
				throw new IndexException(e);
			}
			brb.add(irb);
		}
		BulkResponse response = brb.execute().actionGet();
		if (response.hasFailures())
		{
			String message = response.buildFailureMessage();
			throw new RuntimeException(message);
		}
	}

	public Client getClient()
	{
		return esClient;
	}

}
