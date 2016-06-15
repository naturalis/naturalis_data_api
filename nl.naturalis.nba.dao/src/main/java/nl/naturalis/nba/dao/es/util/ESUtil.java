package nl.naturalis.nba.dao.es.util;

import java.io.InputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.utils.Charsets;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;

import nl.naturalis.nba.dao.es.AbstractDao;
import nl.naturalis.nba.dao.es.Registry;
import nl.naturalis.nba.dao.es.map.MappingSerializer;
import nl.naturalis.nba.dao.es.types.ESType;

public class ESUtil {

	private static final Logger logger = Registry.getInstance().getLogger(ESUtil.class);

	private ESUtil()
	{
	}

	public static String base64Encode(String s)
	{
		byte[] bytes = s.getBytes(Charsets.UTF_8);
		bytes = Base64.getEncoder().encode(bytes);
		return new String(bytes, Charsets.UTF_8);
	}

	public static Set<IndexInfo> getDistinctIndices()
	{
		Set<IndexInfo> result = new HashSet<>(3);
		result.add(DocumentType.SPECIMEN.getIndexInfo());
		result.add(DocumentType.TAXON.getIndexInfo());
		result.add(DocumentType.MULTI_MEDIA_OBJECT.getIndexInfo());
		return result;
	}

	public static void createIndices()
	{
		Set<IndexInfo> indices = getDistinctIndices();
		for (IndexInfo index : indices) {
			createIndex(index);
		}
	}

	private static void createIndex(IndexInfo indexInfo)
	{
		String index = indexInfo.getName();
		logger.info("Creating index {}", index);
		// First load non-user-configurable settings
		String resource = "/es-settings.json";
		InputStream is = AbstractDao.class.getResourceAsStream(resource);
		Builder builder = Settings.settingsBuilder();
		builder.loadFromStream(resource, is);
		// Then add user-configurable settings
		builder.put("index.number_of_shards", indexInfo.getNumShards());
		builder.put("index.number_of_replicas", indexInfo.getNumReplicas());
		CreateIndexRequestBuilder request = indices().prepareCreate(index);
		request.setSettings(builder.build());
		CreateIndexResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create index " + index);
		}
		logger.info("Created index {}", index);
	}

	private static void createType(DocumentType documentType)
	{
		String index = documentType.getIndexInfo().getName();
		String type = documentType.getName();
		logger.info("Creating type {}", type);
		PutMappingRequestBuilder request = indices().preparePutMapping(index);
		MappingSerializer serializer = MappingSerializer.getInstance();
		String source = serializer.serialize(documentType.getMapping());
		request.setSource(source);
		request.setType(type);
		PutMappingResponse response = request.execute().actionGet();
		if (!response.isAcknowledged()) {
			throw new RuntimeException("Failed to create type " + type);
		}
		logger.info("Created type {}", type);
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
		return Registry.getInstance().getESClientFactory().getClient();
	}

}
