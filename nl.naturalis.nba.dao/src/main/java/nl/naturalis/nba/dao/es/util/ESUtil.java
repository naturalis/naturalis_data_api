package nl.naturalis.nba.dao.es.util;

import java.io.InputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.compress.utils.Charsets;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.IndexNotFoundException;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.dao.es.AbstractDao;
import nl.naturalis.nba.dao.es.ESClientManager;
import nl.naturalis.nba.dao.es.IndexInfo;
import nl.naturalis.nba.dao.es.DAORegistry;
import nl.naturalis.nba.dao.es.DocumentType;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.map.MappingSerializer;

public class ESUtil {

	private static final Logger logger = DAORegistry.getInstance().getLogger(ESUtil.class);

	private ESUtil()
	{
	}

	public static String base64Encode(String s)
	{
		byte[] bytes = s.getBytes(Charsets.UTF_8);
		bytes = Base64.getEncoder().encode(bytes);
		return new String(bytes, Charsets.UTF_8);
	}

	/**
	 * Generates the value of the Elasticsearch _id field based on the source
	 * system of the record and the id the record had in the source system.
	 * Values for the _id field are never auto-generated by Elasticsearch.
	 * Instead, the value is generated as follows:
	 * {@code sourceSystemId + '@' + sourceSystem.getCode()}.
	 * 
	 * @param sourceSystem
	 * @param sourceSystemId
	 * @return
	 */
	public static String getElasticsearchId(SourceSystem sourceSystem, String sourceSystemId)
	{
		return sourceSystemId + '@' + sourceSystem.getCode();
	}

	/**
	 * Generates the value of the Elasticsearch _id field based on the source
	 * system of the record and the id the record had in the source system.
	 * 
	 * @param sourceSystem
	 * @param sourceSystemId
	 * @return
	 */
	public static String getElasticsearchId(SourceSystem sourceSystem, int sourceSystemId)
	{
		return sourceSystemId + '@' + sourceSystem.getCode();
	}

	/**
	 * Returns the indices for all public static final {@link DocumentType}
	 * instances defined in the {@link DocumentType} class. Document types may
	 * share an index, but this method only returns unique indices.
	 * 
	 * @return
	 */
	public static Set<IndexInfo> getDistinctIndices()
	{
		Set<IndexInfo> result = new HashSet<>(3);
		result.add(DocumentType.SPECIMEN.getIndexInfo());
		result.add(DocumentType.TAXON.getIndexInfo());
		result.add(DocumentType.MULTI_MEDIA_OBJECT.getIndexInfo());
		return result;
	}

	/**
	 * Deletes all indices used by the NBA. All data will be lost. WATCH OUT!
	 */
	public static void deleteAllIndices()
	{
		for (IndexInfo index : getDistinctIndices()) {
			deleteIndex(index);
		}
	}

	/**
	 * Creates all Elasticsearch indices for all public static final
	 * {@link DocumentType} instances defined in the {@link DocumentType} class.
	 */
	public static void createAllIndices()
	{
		for (IndexInfo index : getDistinctIndices()) {
			createIndex(index);
		}
	}

	private static void deleteIndex(IndexInfo indexInfo)
	{
		String index = indexInfo.getName();
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
			throw new DaoException("Failed to create index " + index);
		}
		logger.info("Created index {}", index);
		for (DocumentType dt : indexInfo.getTypes()) {
			createType(dt);
		}
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
			throw new DaoException("Failed to create type " + type);
		}
		logger.info("Created type {}", type);
	}

	private static IndicesAdminClient indices()
	{
		return ESClientManager.getInstance().getClient().admin().indices();
	}

}
