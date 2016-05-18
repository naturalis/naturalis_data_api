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
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.IndexNotFoundException;

import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.es.Registry;
import nl.naturalis.nba.dao.es.map.Mapping;
import nl.naturalis.nba.dao.es.map.MappingFactory;
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESType;

public class ESTestUtils {

	private static final Registry registry;
	private static final Logger logger;

	static {
		registry = Registry.getInstance();
		logger = registry.getLogger(ESTestUtils.class);
	}

	public static void createIndex(Class<? extends ESType> cls)
	{
		String index = registry.getIndex(cls);
		logger.info("Creating index {}", index);
		CreateIndexRequestBuilder request = indices().prepareCreate(index);
		Builder builder = Settings.settingsBuilder();
		File settingsFile = registry.getFile("es-settings.json");
		logger.info("Reading Elasticsearch settings from " + settingsFile.getAbsolutePath());
		String settings = FileUtil.getContents(settingsFile);
		builder.loadFromSource(settings);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + index);
		}
		logger.info("Created index {}", index);
	}

	public static void dropIndex(Class<? extends ESType> cls)
	{
		String index = registry.getIndex(cls);
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
		String index = registry.getIndex(cls);
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
		String index = registry.getIndex(obj.getClass());
		String type = registry.getType(obj.getClass());
		String source = JsonUtil.toJson(obj);
		IndexRequestBuilder irb = client().prepareIndex(index, type);
		if (id != null) {
			irb.setId(id);
		}
		if (parentId != null) {
			irb.setParent(parentId);
		}
		irb.setSource(source);
		irb.execute().actionGet();
	}

	public static void refreshIndex(Class<? extends ESType> cls)
	{
		String index = registry.getIndex(cls);
		RefreshRequestBuilder request = indices().prepareRefresh(index);
		request.execute().actionGet();
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
