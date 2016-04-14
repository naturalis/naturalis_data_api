package nl.naturalis.nba.dao;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.IndexNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESType;

public class ESTestUtils {

	/*
	 * Choose short index refresh interval. Unit tests won't be doing that many
	 * CRUD operations and we don't want them to wait (sleep) everytime they do.
	 */
	private static final long INDEX_REFRESH_INTERVAL = 1;

	private static final Registry registry;
	private static final Logger logger;

	static {
		registry = Registry.getInstance();
		logger = registry.getLogger(ESTestUtils.class);
	}

	public static void createIndex(Class<? extends ESType> cls)
	{
		String index = registry.getIndices(cls)[0];
		logger.info("Creating index {}", index);
		CreateIndexRequestBuilder request = indices().prepareCreate(index);
		Builder builder = Settings.settingsBuilder();
		File settingsFile = registry.getFile("es-settings.json");
		String settings = FileUtil.getContents(settingsFile);
		builder.loadFromSource(settings);
		//builder.put("index.refresh_interval", INDEX_REFRESH_INTERVAL);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + index);
		}
		logger.info("Created index {}", index);
	}

	public static void dropIndex(Class<? extends ESType> cls)
	{
		String index = registry.getIndices(cls)[0];
		logger.info("Deleting index {}", index);
		DeleteIndexRequestBuilder request = indices().prepareDelete(index);
		try {
			DeleteIndexResponse response = request.execute().actionGet();
			if (!response.isAcknowledged()) {
				throw new RuntimeException("Failed to delete index " + index);
			}
			logger.info("Index deleted");
		}
		catch (IndexNotFoundException e) {
			logger.info("No such index \"{}\" (nothing deleted)", index);
		}
	}

	public static void createType(Class<? extends ESType> cls)
	{
		String index = registry.getIndices(cls)[0];
		String type = registry.getType(cls);
		logger.info("Creating type {}", type);
		PutMappingRequestBuilder request = indices().preparePutMapping(index);
		request.setSource(getMapping(cls));
		request.setType(type);
		PutMappingResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create type " + type);
		}
		logger.info("Created type {}", type);
	}

	public static void saveObject(ESType object)
	{
		saveObject(null, null, object);
	}

	public static void saveObject(String id, ESType object)
	{
		saveObject(id, null, object);
	}

	public static void saveObject(String id, String parentId, ESType obj)
	{
		String index = registry.getIndices(obj.getClass())[0];
		String type = registry.getType(obj.getClass());
		String source;
		try {
			ObjectMapper om = registry.getObjectMapper(obj.getClass());
			source = om.writeValueAsString(obj);
		}
		catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
		IndexRequestBuilder irb = client().prepareIndex(index, type);
		if (id != null)
			irb.setId(id);
		if (parentId != null)
			irb.setParent(parentId);
		irb.setSource(source);
		irb.execute().actionGet();
	}

	/**
	 * Sleep long enough for the index to have refreshed after a CRUD operation.
	 */
	public static void sleep()
	{
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static IndicesAdminClient indices()
	{
		return admin().indices();
	}

	private static AdminClient admin()
	{
		return client().admin();
	}

	private static Client client()
	{
		return registry.getESClientFactory().getClient();
	}

	private static String getMapping(Class<? extends ESType> cls)
	{
		MappingFactory factory = new MappingFactory();
		Mapping mapping = factory.getMapping(cls);
		MappingSerializer serializer = MappingSerializer.getInstance();
		return serializer.serialize(mapping);
	}

}
