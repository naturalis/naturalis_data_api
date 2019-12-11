package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryFactory.createAggregationQuery;
import static nl.naturalis.nba.dao.aggregation.AggregationType.COUNT_DISTINCT_VALUES;
import static nl.naturalis.nba.dao.aggregation.AggregationType.COUNT_DISTINCT_VALUES_PER_GROUP;
import static nl.naturalis.nba.dao.aggregation.AggregationType.GET_DISTINCT_VALUES;
import static nl.naturalis.nba.dao.aggregation.AggregationType.GET_DISTINCT_VALUES_PER_GROUP;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeCountRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.toDocumentObject;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.aggregation.AggregationQuery;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.es.DirtyDocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;

public abstract class NbaDao<T extends IDocumentObject> implements INbaAccess<T> {

  private static final Logger logger = getLogger(NbaDao.class);

  private final DocumentType<T> dt;

  NbaDao(DocumentType<T> dt) {
    this.dt = dt;
  }

  public static void ping() {
    ESUtil.esClient();
  }

  @Override
  public T find(String id) {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("find", id));
    }
    String index = dt.getIndexInfo().getName();
    SearchRequest request = new SearchRequest();
    request.indices(index);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    
    // When aliases are being used, searching for an id needs to be done a term query
    if (Boolean.parseBoolean(DaoRegistry.getInstance().getConfiguration().get("elasticsearch.aliases", "false") )) {
      String[] ids = new String[] {id};
      IdsQueryBuilder query = QueryBuilders.idsQuery();
      query.addIds(ids);
      searchSourceBuilder.query(query);
      request.source(searchSourceBuilder);
      SearchResponse response;
      try {
        response = ESUtil.esClient().search(request, RequestOptions.DEFAULT);
        T[] docs = processQueryResponse(response);
        if (docs.length == 0)
          return null;
        else if (docs.length == 1) {
          return docs[0];
        } else {
          logger.debug("{} with id \"{}\" found in more than one index", dt, id);
          String msg = String.format("The given id \"%s\"has been found in more that one index, which is not allowed", id);
          throw new DaoException(msg);        
        }      
      } catch (IOException e) {
        throw new DaoException(e.getMessage());
      }
    }
    // If no aliases are used, the document can be accessed directly using the "document path"
    GetRequest getRequest = new GetRequest(index, id);
    T obj = null;
    try {
      GetResponse response = ESUtil.esClient().get(getRequest, RequestOptions.DEFAULT);
      byte[] json = BytesReference.toBytes(response.getSourceAsBytesRef());
      obj = JsonUtil.deserialize(dt.getObjectMapper(), json, dt.getJavaType());
      obj.setId(id);
      return obj;
    } catch (IOException e) {
      throw new DaoException(e.getMessage());
    }
  }

  @Override
  public T[] findByIds(String[] ids) {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("find", ids));
    }
    if (ids.length > 1024) {
      String fmt = "Number of ids to look up exceeds maximum of 1024: %s";
      String msg = String.format(fmt, ids.length);
      throw new DaoException(msg);
    }
    SearchRequest request = newSearchRequest(dt);
    IdsQueryBuilder query = QueryBuilders.idsQuery();
    query.addIds(ids);   
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder(); 
    sourceBuilder.query(query);
    sourceBuilder.size(ids.length);
    request.source(sourceBuilder);        
    return processSearchRequest(request);
  }

  @Override
  public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("query", querySpec));
    }
    QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
    return createSearchResult(translator);
  }

  @Override
  public long count(QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("count", querySpec));
    }
    QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
    return createCountResult(translator);  
  }

  @SuppressWarnings("unchecked")
  @Override
  public long countDistinctValues(String forField, QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("countDistinctValues", forField, querySpec));
    }
    AggregationQuery<T, Long> aggregationQuery = 
        (AggregationQuery<T, Long>) createAggregationQuery(COUNT_DISTINCT_VALUES, dt, forField, null, querySpec);
    return aggregationQuery.getResult().longValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Map<String, Object>> countDistinctValuesPerGroup(String forGroup, String forField,
      QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("countDistinctValuesPerGroup", forField, forGroup, querySpec));
    }
    AggregationQuery<T, List<Map<String, Object>>> aggregationQuery =
        (AggregationQuery<T, List<Map<String, Object>>>) createAggregationQuery(
            COUNT_DISTINCT_VALUES_PER_GROUP, dt, forField, forGroup, querySpec);
    return aggregationQuery.getResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Map<String, Long> getDistinctValues(String forField, QuerySpec querySpec)
      throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("getDistinctValues", forField, querySpec));
    }
    AggregationQuery<T, Map<String, Long>> aggregationQuery =
        (AggregationQuery<T, Map<String, Long>>) createAggregationQuery(GET_DISTINCT_VALUES, dt,
            forField, null, querySpec);
    return aggregationQuery.getResult();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Map<String, Object>> getDistinctValuesPerGroup(String forGroup, String forField, QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("getDistinctValuesPerGroup", forField, forGroup, querySpec));
    }
    AggregationQuery<T, List<Map<String, Object>>> aggregationQuery =
        (AggregationQuery<T, List<Map<String, Object>>>) createAggregationQuery(GET_DISTINCT_VALUES_PER_GROUP, dt, forField, forGroup, querySpec);
    return aggregationQuery.getResult();
  }

  public String save(T apiObject, boolean immediate) {
    String id = apiObject.getId();
    apiObject.setId(null);
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    if (logger.isDebugEnabled()) {
      String pattern = "New save request (index={};type={};id={})";
      logger.debug(pattern, index, type, id);
    }

    IndexRequest request = new IndexRequest();
    byte[] source = JsonUtil.serialize(apiObject);
    request.source(source, XContentType.JSON);
    IndexResponse response;
    try {
      response = ESUtil.esClient().index(request, RequestOptions.DEFAULT);
      if (immediate) {
        RefreshRequest refreshRequest = new RefreshRequest(index);
        ESUtil.esClient().indices().refresh(refreshRequest, RequestOptions.DEFAULT);
      }
      apiObject.setId(response.getId());
      return response.getId();
    } catch (IOException e) {
      throw new DaoException(String.format("Failed to save object with id %s: %s", id, e.getMessage()));
    }
  }

  public boolean delete(String id, boolean immediate) {
    String index = dt.getIndexInfo().getName();
    boolean deleted = false;

    DeleteRequest request = new DeleteRequest(index, id);
    try {
      DeleteResponse deleteResponse = ESUtil.esClient().delete(request, RequestOptions.DEFAULT);
      logger.info("Deleted document with id \"{}\" from index \"{}\"", deleteResponse.getId(), deleteResponse.getIndex());
      deleted = true;
    } catch (IOException e) {
      String msg = String.format("Failed to delete document with id \"%s\" from index \"%s\"", index, id);
      logger.error(msg);
      throw new DaoException(msg);
    }
    return deleted;
  }

  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException, IOException {

    if (logger.isDebugEnabled()) {
      logger.debug(printCall("downloadQuery", querySpec, out));
    }

    DirtyDocumentIterator<T> iterator = new DirtyDocumentIterator<>(dt, querySpec);
    Writer writer = new BufferedWriter(new OutputStreamWriter(out), 4096);

    /*
     * The output will be in NDJSON format which means:
     * - no square brackets (at beginning / end)
     * - one object per line, 
     * - delimited by a new line character
     */
    try {
      while (iterator.hasNext()) {
        writer.write(JsonUtil.toJson(iterator.next()));
        if (iterator.hasNext()) {
          writer.write(System.lineSeparator());
        }
        writer.flush();
      }
    } catch (IOException e) {
      throw new DaoException(e);
    }
  }

  abstract T[] createDocumentObjectArray(int length);

  T[] processSearchRequest(SearchRequest request) {
    SearchResponse response = executeSearchRequest(request);
    return processQueryResponse(response);
  }

  private List<QueryResultItem<T>> createItems(SearchResponse response) {
    if (logger.isDebugEnabled()) {
      String type = dt.getJavaType().getSimpleName();
      logger.debug("Converting search hits to {} instances", type);
    }
    SearchHit[] hits = response.getHits().getHits();
    List<QueryResultItem<T>> items = new ArrayList<>(hits.length);
    for (SearchHit hit : hits) {
      T obj = toDocumentObject(hit, dt);
      items.add(new QueryResultItem<>(obj, hit.getScore()));
    }
    return items;
  }

  private QueryResult<T> createSearchResult(QuerySpecTranslator translator) throws InvalidQueryException {
    SearchRequest request = translator.translate();    
    SearchResponse response = executeSearchRequest(request);    
    QueryResult<T> result = new QueryResult<>();
    result.setResultSet(createItems(response));
    
    CountRequest countRequest = translator.translateCountRequest();
    CountResponse countResponse = executeCountRequest(countRequest);
    result.setTotalSize(countResponse.getCount());
    
    return result;
  }
  
  private long createCountResult(QuerySpecTranslator translator) throws InvalidConditionException {
    CountRequest request = translator.translateCountRequest();
    CountResponse response = executeCountRequest(request);
    return response.getCount();
  }

  private T[] processQueryResponse(SearchResponse response) {
    SearchHit[] hits = response.getHits().getHits();
    T[] documentObjects = createDocumentObjectArray(hits.length);
    for (int i = 0; i < hits.length; ++i) {
      documentObjects[i] = toDocumentObject(hits[i], dt);
    }
    return documentObjects;
  }

}