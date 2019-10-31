package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.utils.StringUtil.fromInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.AcknowledgedResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.common.es.map.MappingSerializer;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.IndexInfo;
import nl.naturalis.nba.dao.exception.DaoException;

/**
 * Methods for interacting with Elasticsearch, mostly intended to be used for unit testing and by
 * the data import programs in {@code nl.naturalis.nba.etl}.
 * 
 * @author Ayco Holleman
 *
 */
public class ESUtil {

  private static final Logger logger = getLogger(ESUtil.class);

  private ESUtil() {}

  /**
   * Returns an Elasticsearch {@code Client} object.
   * 
   * @return
   */
  public static RestHighLevelClient esClient() {
    return ESClientManager.getInstance().getClient();
  }

  /**
   * Converts the specified {@link SearchHit} to a instance of T.
   * 
   * @param hit
   * @param dt
   * @return
   */
  public static <T extends IDocumentObject> T toDocumentObject(SearchHit hit, DocumentType<T> dt) {
    byte[] json;
    if (hit.getSourceRef() == null) {
      /*
       * This happens if the user specified a zero-size List for QuerySpec.fields. See
       * QuerySpecTranslator.
       */
      json = "{}".getBytes();
    } else {
      json = BytesReference.toBytes(hit.getSourceRef());
    }
    T obj = JsonUtil.deserialize(dt.getObjectMapper(), json, dt.getJavaType());
    obj.setId(hit.getId());
    return obj;
  }

  /**
   * Prepares a new search request for the specified document type.
   * 
   * @param dt
   * @return
   */
  public static SearchRequest newSearchRequest(DocumentType<?> dt) {
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    if (logger.isDebugEnabled()) {
      logger.debug("New search request: {}/{}", index, type);
    }
    return new SearchRequest(index);
  }

  /**
   * Executes the specified search request.
   * 
   * @param request
   * @return
   */
//  ES5
//  public static SearchResponse executeSearchRequest(SearchRequestBuilder request) {
//    if (logger.isDebugEnabled()) {
//      logger.debug("Executing search request:\n{}", request);
//    }
//    SearchResponse response = request.get();
//    if (logger.isDebugEnabled()) {
//      logger.debug("Documents found: {}", response.getHits().getTotalHits());
//    }
//    return response;
//  }
// ES7
  public static SearchResponse executeSearchRequest(SearchRequest request) {
    if (logger.isDebugEnabled()) {
      logger.debug("Executing search request:\n{}", request);
    }
    SearchResponse response = null;
    try {
      response = esClient().search(request, RequestOptions.DEFAULT);
      if (logger.isDebugEnabled()) {
        logger.debug("Documents found: {}", response.getHits().getTotalHits());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return response;
  }

  
  /**
   * Generates the value of the Elasticsearch _id field based on the source system of the record and
   * the id the record had in the source system. Values for the _id field are never auto-generated
   * by Elasticsearch. Instead, the value is generated as follows:
   * {@code sourceSystemId + '@' + sourceSystem.getCode()}.
   * 
   * @param sourceSystem
   * @param sourceSystemId
   * @return
   */
  public static String getElasticsearchId(SourceSystem sourceSystem, String sourceSystemId) {
    return sourceSystemId + '@' + sourceSystem.getCode();
  }

  /**
   * Generates the value of the Elasticsearch _id field based on the source system of the record and
   * the id the record had in the source system.
   * 
   * @param sourceSystem
   * @param sourceSystemId
   * @return
   */
  public static String getElasticsearchId(SourceSystem sourceSystem, int sourceSystemId) {
    return String.valueOf(sourceSystemId) + '@' + sourceSystem.getCode();
  }

  /**
   * Returns all indices hosting NBA {@link DocumentType document types}. Document types may share
   * an index, but this method only returns unique indices.
   * 
   * @return
   */
  public static Set<IndexInfo> getDistinctIndices() {
    Set<IndexInfo> result = new HashSet<>();
    for (DocumentType<?> dt : DocumentType.getAllDocumentTypes()) {
      result.add(dt.getIndexInfo());
    }
    return result;
  }

  /**
   * Returns the indices hosting the specified NBA document types. Document types may share an
   * index, but this method only returns unique indices.
   * 
   * @param documentTypes
   * @return
   */
  public static Set<IndexInfo> getDistinctIndices(DocumentType<?>... documentTypes) {
    Set<IndexInfo> result = new HashSet<>();
    for (DocumentType<?> dt : documentTypes) {
      result.add(dt.getIndexInfo());
    }
    return result;
  }

  /**
   * Deletes all Elasticsearch indices used by the NBA. Watch out! All NBA data will be lost.
   */
  public static void deleteAllIndices() {
    for (IndexInfo index : getDistinctIndices()) {
      deleteIndex(index);
    }
  }

  /**
   * Creates all Elasticsearch indices used by the NBA.
   */
  public static void createAllIndices() {
    for (IndexInfo index : getDistinctIndices()) {
      createIndex(index);
    }
  }

  /**
   * Deletes the Elasticsearch index hosting the specified {@link DocumentType document type}.
   * 
   * @param documentType
   */
  public static void deleteIndex(DocumentType<?> documentType) {
    deleteIndex(documentType.getIndexInfo());
  }

  /**
   * Deletes the specified Elasticsearch indices.
   * 
   * @param documentType
   */
  public static void deleteIndices(Set<IndexInfo> indices) {
    for (IndexInfo index : indices) {
      deleteIndex(index);
    }
  }

  /**
   * Deletes the specified Elasticsearch index.
   * 
   * @param indexInfo
   */
  public static void deleteIndex(IndexInfo indexInfo) {
    deleteIndex(indexInfo.getName());
  }

  /**
   * Deletes the specified Elasticsearch index.
   * 
   * @param indexInfo
   */
  public static void deleteIndex(String index) {
    logger.info("Deleting index {}", index);

    // ES5
//    DeleteIndexRequestBuilder request = indices().prepareDelete(index);
//    try {
//      DeleteIndexResponse response = request.execute().actionGet();
//      if (!response.isAcknowledged()) {
//        throw new RuntimeException("Failed to delete index " + index);
//      }
//      logger.info("Index deleted");
//    } catch (IndexNotFoundException e) {
//      logger.info("No such index \"{}\" (nothing deleted)", index);
//    }

    // ES7
    try {
      DeleteIndexRequest request = new DeleteIndexRequest(index);
      esClient().indices().delete(request, RequestOptions.DEFAULT);
      logger.info("Index deleted");
    } catch (ElasticsearchException exception) {
      if (exception.status() == RestStatus.NOT_FOUND) {
        logger.info("No such index \"{}\" (nothing deleted)", index);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      logger.info("No such index \"{}\" (nothing deleted)", index);
    }

  }

  /**
   * Creates an Elasticsearch index for the specified {@link DocumentType document type}.
   * 
   * @param documentType
   */
  public static void createIndex(DocumentType<?> documentType) {
    createIndex(documentType.getIndexInfo());
  }

  /**
   * Creates the specified indices <i>plus</i> all document types they are configured to host.
   * 
   * @param indices
   */
  public static void createIndices(Set<IndexInfo> indices) {
    for (IndexInfo index : indices) {
      createIndex(index);
    }
  }

  /**
   * Creates the specified index <i>plus</i> all document types it is configured to host.
   * 
   * @param indexInfo
   */
  public static void createIndex(IndexInfo indexInfo) {
    String index = indexInfo.getName();
    logger.info("Creating index {}", index);
    // First load non-user-configurable settings
    String resource = "/es-settings.json";
    InputStream is = ESUtil.class.getResourceAsStream(resource);
    
    // ES5
//    Builder builder = Settings.builder();
//    try {
//      builder.loadFromStream(resource, is);
//    } catch (IOException e) {
//      throw new DaoException(e);
//    }
//    // Then add user-configurable settings
//    builder.put("index.number_of_shards", indexInfo.getNumShards());
//    builder.put("index.number_of_replicas", indexInfo.getNumReplicas());
//    CreateIndexRequestBuilder request = indices().prepareCreate(index);
//    request.setSettings(builder.build());
//    CreateIndexResponse response = request.execute().actionGet();
//    if (!response.isAcknowledged()) {
//      throw new DaoException("Failed to create index " + index);
//    }
//    for (DocumentType<?> dt : indexInfo.getTypes()) {
//      createType(dt);
//    }
    
    // ES7
    CreateIndexRequest request = new CreateIndexRequest(index); 
    String result = fromInputStream(is);
    request.settings(result, XContentType.JSON);
    request.settings(Settings.builder() 
        .put("index.number_of_shards", indexInfo.getNumShards())
        .put("index.number_of_replicas", indexInfo.getNumReplicas())
    );
    try {
      esClient().indices().create(request, RequestOptions.DEFAULT);
      logger.info("Index {} created", index);
    } catch (IOException e) {
      throw new DaoException("Failed to create index " + index);
    }
    
  }

  /**
   * Returns the value of the specified Elasticsearch index setting.
   * 
   * @param dt
   * @param setting
   * @return
   */
  public static String getIndexSetting(DocumentType<?> dt, String setting) {
    return getIndexSetting(dt.getIndexInfo(), setting);
  }

  /**
   * Returns the value of the specified Elasticsearch index setting
   * 
   * @param indexInfo
   * @param setting
   * @return
   */
  public static String getIndexSetting(IndexInfo indexInfo, String setting) {
    String index = indexInfo.getName();
    GetSettingsRequest request = new GetSettingsRequest();
    GetSettingsResponse response = indices().getSettings(request).actionGet();
    try {
      return response.getSetting(index, setting);
    }
    /*
     * Hack to work around a nasty feature in Elasticsearch (2.3.3). A NullPointerException is
     * thrown if the index does not exist, or if no settings have been explicitly set for it.
     */
    catch (NullPointerException e) {
      return null;
    }
  }

  /**
   * Returns the values of the specified Elasticsearch index settings.
   * 
   * @param dt
   * @param settings
   * @return
   */
  public static Map<String, String> getIndexSettings(DocumentType<?> dt, String... settings) {
    return getIndexSettings(dt.getIndexInfo(), settings);
  }

  /**
   * Returns the values of the specified Elasticsearch index settings.
   * 
   * @param indexInfo
   * @param settings
   * @return
   */
  public static Map<String, String> getIndexSettings(IndexInfo indexInfo, String... settings) {
    String index = indexInfo.getName();
    GetSettingsRequest request = new GetSettingsRequest();
    GetSettingsResponse response = indices().getSettings(request).actionGet();
    LinkedHashMap<String, String> result = new LinkedHashMap<>();
    for (String setting : settings) {
      try {
        result.put(setting, response.getSetting(index, setting));
      } catch (NullPointerException e) {
        result.put(setting, null);
      }
    }
    return result;
  }

  /**
   * Refreshes the index hosting the specified {@link DocumentType document type} (forcing all
   * imported data to become "visible").
   * 
   * @param documentType
   */
  public static void refreshIndex(DocumentType<?> documentType) {
    refreshIndex(documentType.getIndexInfo());
  }

  /**
   * Refreshed the specified index (forcing all imported data to become "visible").
   * 
   * @param indexInfo
   */
  public static void refreshIndex(IndexInfo indexInfo) {
    String index = indexInfo.getName();
    RefreshRequestBuilder request = indices().prepareRefresh(index);
    request.execute().actionGet();
    RefreshResponse response = request.execute().actionGet();
    if (response.getFailedShards() != 0) {
      logger.error("Index refresh failed index " + indexInfo.getName());
    }
  }

  /**
   * Returns the index refresh interval for the specified index.
   * 
   * @param indexInfo
   * @return
   */
  public static String getAutoRefreshInterval(IndexInfo indexInfo) {
    return getIndexSetting(indexInfo, "index.refresh_interval");
  }

  /**
   * Sets the index refresh interval to "-1" for the specified index and returns the original
   * refresh interval
   * 
   * @param indexInfo
   * @return The original refresh interval
   */
  public static Object disableAutoRefresh(IndexInfo indexInfo) {
    String index = indexInfo.getName();
    logger.info("Disabling auto-refresh for index " + index);
    Object origValue = getAutoRefreshInterval(indexInfo);
    UpdateSettingsRequest request = new UpdateSettingsRequest(index);
    Builder builder = Settings.builder();
    builder.put("index.refresh_interval", -1);
    request.settings(builder.build());
    UpdateSettingsResponse response = indices().updateSettings(request).actionGet();
    if (!response.isAcknowledged()) {
      String msg = "Failed to disable auto-refresh for index " + index;
      throw new DaoException(msg);
    }
    return origValue;
  }

  /**
   * Sets the index refresh interval for the specified index.
   * 
   * @param indexInfo
   * @param interval
   */
  public static void setAutoRefreshInterval(IndexInfo indexInfo, String interval) {
    if (interval == null) {
      logger.warn("Setting the index refresh interval to null has no effect");
      return;
    }
    String index = indexInfo.getName();
    logger.info("Updating index refresh interval for index " + index);
    UpdateSettingsRequest request = new UpdateSettingsRequest(index);
    Builder builder = Settings.builder();
    builder.put("index.refresh_interval", interval);
    request.settings(builder.build());
    UpdateSettingsResponse response = indices().updateSettings(request).actionGet();
    if (!response.isAcknowledged()) {
      String msg = "Failed to update index refresh interval for index " + index;
      throw new DaoException(msg);
    }
  }

  /**
   * Creates a type mapping for the specified {@link DocumentType document type}.
   * 
   * @param dt
   */
  public static <T extends IDocumentObject> void createType(DocumentType<T> dt) {
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    logger.info("Creating type {} in index {}", type, index);
    PutMappingRequestBuilder request = indices().preparePutMapping(index);
    MappingSerializer<T> serializer = new MappingSerializer<>();
    String source = serializer.serialize(dt.getMapping());
    request.setSource(source, XContentType.JSON);
    request.setType(type);
    try {
      PutMappingResponse response = request.execute().actionGet();
      if (!response.isAcknowledged()) {
        throw new DaoException("Failed to create type " + type);
      }
    } catch (Throwable t) {
      String fmt = "Failed to create type %s: %s";
      String msg = String.format(fmt, type, t.getMessage());
      if (logger.isDebugEnabled()) {
        logger.debug(t);
      }
      throw new DaoException(msg);
    }
  }

  /**
   * Deletes all documents of the specified document type. Once the documents are deleted the index
   * is refreshed.
   * 
   * @param dt The type of the documents to be deleted
   */
  public static <T extends IDocumentObject> void truncate(DocumentType<T> dt) {
    logger.info("Deleting all {} documents", dt.getName());
    QuerySpec qs = new QuerySpec();
    qs.setConstantScore(true);
    qs.setFields(Collections.emptyList());
    qs.setSize(1000);
    DirtyDocumentIterator<T> extractor = new DirtyDocumentIterator<>(dt, qs);
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    Client client = ESClientManager.getInstance().getClient();
    Collection<T> batch = extractor.nextBatch();
    while (batch != null) {
      BulkRequestBuilder brb = client.prepareBulk();
      for (T obj : batch) {
        brb.add(new DeleteRequest(index, type, obj.getId()));
      }
      BulkResponse response = brb.get();
      if (response.hasFailures()) {
        throw new DaoException("Error while deleting documents from " + type);
      }
      batch = extractor.nextBatch();
    }
    refreshIndex(dt);
    logger.info("Documents deleted: {}", extractor.size());
  }

  /**
   * Deletes all documents of the specified document type and the specified source system. Once the
   * documents are deleted the index is refreshed.
   * 
   * @param dt The type of the documents to be deleted
   * @param ss The source system of the documents to be deleted
   */
  public static <T extends IDocumentObject> void truncate(DocumentType<T> dt, SourceSystem ss) {
    logger.info("Deleting all {} {} documents", ss.getCode(), dt.getName());
    QuerySpec qs = new QuerySpec();
    qs.setConstantScore(true);
    qs.setFields(Collections.emptyList());
    qs.setSize(1000);
    qs.addCondition(new QueryCondition("sourceSystem.code", "=", ss.getCode()));
    DirtyDocumentIterator<T> extractor = new DirtyDocumentIterator<>(dt, qs);
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    Client client = ESClientManager.getInstance().getClient();
    Collection<T> batch = extractor.nextBatch();
    while (batch != null) {
      BulkRequestBuilder brb = client.prepareBulk();
      for (T obj : batch) {
        brb.add(new DeleteRequest(index, type, obj.getId()));
      }
      BulkResponse response = brb.get();
      if (response.hasFailures()) {
        throw new DaoException("Error while deleting documents from " + type);
      }
      batch = extractor.nextBatch();
    }
    refreshIndex(dt);
    logger.info("Documents deleted: {}", extractor.size());
  }

  /**
   * Utility method for retrieving metadata about the ETL process and the source files from the
   * document store.
   * 
   * @return HashMap<String, String>
   */
  public static Map<String, Object> getNbaMetadata() {
    Client client = esClient();
    String index = "meta";
    String documentType = "importdata";
    String _id = "importdata";
    GetResponse query = client.prepareGet(index, documentType, _id).get();
    return query.getSourceAsMap();
  }

  private static IndicesAdminClient indices() {
    return ESClientManager.getInstance().getClient().admin().indices();
  }

}
