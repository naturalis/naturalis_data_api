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
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.IndexNotFoundException;

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

	public static void createIndex(String name)
	{
		logger.info("Creating index {}", name);
		Client client = registry.getESClientFactory().getClient();
		IndicesAdminClient admin = client.admin().indices();
		CreateIndexRequestBuilder request = admin.prepareCreate(name);
		Builder builder = Settings.settingsBuilder();
		File settingsFile = registry.getFile("es-settings.json");
		String settings = FileUtil.getContents(settingsFile);
		builder.loadFromSource(settings);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + name);
		}
		logger.info("Created index {}", name);
	}

	public static void dropIndex(String name)
	{
		logger.info("Deleting index {}", name);
		Client client = registry.getESClientFactory().getClient();
		IndicesAdminClient admin = client.admin().indices();
		DeleteIndexRequestBuilder request = admin.prepareDelete(name);
		try {
			DeleteIndexResponse response = request.execute().actionGet();
			if (!response.isAcknowledged()) {
				throw new RuntimeException("Failed to delete index " + name);
			}
			logger.info("Index deleted");
		}
		catch (IndexNotFoundException e) {
			logger.info("No such index \"{}\" (nothing deleted)", name);
		}
	}

	public static void createType(String index, String type, Class<? extends ESType> cls)
	{
		logger.info("Creating type {}", type);
		Client client = registry.getESClientFactory().getClient();
		IndicesAdminClient admin = client.admin().indices();
		PutMappingRequestBuilder request = admin.preparePutMapping(index);
		request.setSource(getMapping(cls));
		request.setType(type);
		PutMappingResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create type " + type);
		}
		logger.info("Created type {}", type);
	}
	
	public static void save(ESType instance, String type) {
		// Zie IndexManagerNative
	}

	private static String getMapping(Class<? extends ESType> cls)
	{
		MappingFactory factory = new MappingFactory();
		Mapping mapping = factory.getMapping(cls);
		MappingSerializer serializer = MappingSerializer.getInstance();
		return serializer.serialize(mapping);
	}

}
