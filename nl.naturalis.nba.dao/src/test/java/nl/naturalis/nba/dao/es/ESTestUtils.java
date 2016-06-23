package nl.naturalis.nba.dao.es;

import java.io.InputStream;

import org.apache.logging.log4j.Logger;
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
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESSpecimen;
import nl.naturalis.nba.dao.es.types.ESType;

public class ESTestUtils {

	private static final DAORegistry registry;
	private static final Logger logger;

	static {
		registry = DAORegistry.getInstance();
		logger = registry.getLogger(ESTestUtils.class);
	}

	public static void createIndex(DocumentType dt)
	{
		String index = dt.getIndexInfo().getName();
		logger.info("Creating index {}", index);
		// First load non-user-configurable settings
		String resource = "/es-settings.json";
		InputStream is = DAORegistry.class.getResourceAsStream(resource);
		Builder builder = Settings.settingsBuilder();
		builder.loadFromStream(resource, is);
		// Then add user-configurable settings
		builder.put("index.number_of_shards", dt.getIndexInfo().getNumShards());
		builder.put("index.number_of_replicas", dt.getIndexInfo().getNumReplicas());
		CreateIndexRequestBuilder request = indices().prepareCreate(index);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + index);
		}
		logger.info("Created index {}", index);
	}

	public static void dropIndex(DocumentType dt)
	{
		String index = dt.getIndexInfo().getName();
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

	public static void createType(DocumentType dt)
	{
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		logger.info("Creating type {}", type);
		PutMappingRequestBuilder request = indices().preparePutMapping(index);
		MappingSerializer serializer = new MappingSerializer(true);
		String source = serializer.serialize(dt.getMapping());
		logger.debug("Mapping:\n" + source);
		request.setSource(source);
		request.setType(type);
		PutMappingResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create type " + type);
		}
		logger.info("Created type {}", type);
	}

	public static void saveSpecimens(ESSpecimen... specimens)
	{
		for (ESSpecimen specimen : specimens) {
			saveSpecimen(specimen, false);
		}
		refreshIndex(ESSpecimen.class);
	}

	public static void saveSpecimen(ESSpecimen specimen, boolean refreshIndex)
	{
		String id = specimen.getUnitID() + "@" + specimen.getSourceSystem().getCode();
		saveObject(id, null, specimen, refreshIndex);
	}

	public static void saveObject(ESType object, boolean refreshIndex)
	{
		saveObject(null, null, object, refreshIndex);
	}

	public static void saveObject(String id, ESType object, boolean refreshIndex)
	{
		saveObject(id, null, object, refreshIndex);
	}

	public static void saveObject(String id, String parentId, ESType obj, boolean refreshIndex)
	{
		DocumentType dt = DocumentType.forClass(obj.getClass());
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
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
		if (refreshIndex) {
			refreshIndex(obj.getClass());
		}
	}

	public static void refreshIndex(Class<? extends ESType> cls)
	{
		IndexInfo indexInfo = DocumentType.forClass(cls).getIndexInfo();
		String index = indexInfo.getName();
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
		return ESClientManager.getInstance().getClient();
	}

}
