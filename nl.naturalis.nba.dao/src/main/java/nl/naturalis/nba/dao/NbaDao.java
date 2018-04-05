package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryFactory.createAggregationQuery;
import static nl.naturalis.nba.dao.aggregation.AggregationType.COUNT;
import static nl.naturalis.nba.dao.aggregation.AggregationType.COUNT_DISTINCT_VALUES;
import static nl.naturalis.nba.dao.aggregation.AggregationType.COUNT_DISTINCT_VALUES_PER_GROUP;
import static nl.naturalis.nba.dao.aggregation.AggregationType.GET_DISTINCT_VALUES;
import static nl.naturalis.nba.dao.aggregation.AggregationType.GET_DISTINCT_VALUES_PER_GROUP;
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
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.aggregation.AggregationQuery;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.format.dwca.IDwcaWriter;
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
    GetRequestBuilder request = ESUtil.esClient().prepareGet();
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    request.setIndex(index);
    request.setType(type);
    request.setId(id);
    GetResponse response = request.execute().actionGet();
    if (!response.isExists()) {
      if (logger.isDebugEnabled()) {
        logger.debug("{} with id \"{}\" not found", dt, id);
      }
      return null;
    }
    byte[] json = BytesReference.toBytes(response.getSourceAsBytesRef());
    T obj = JsonUtil.deserialize(dt.getObjectMapper(), json, dt.getJavaType());
    obj.setId(id);
    return obj;
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
    String type = dt.getName();
    SearchRequestBuilder request = newSearchRequest(dt);
    IdsQueryBuilder query = QueryBuilders.idsQuery(type);
    query.addIds(ids);
    request.setQuery(query);
    request.setSize(ids.length);
    return processSearchRequest(request);
  }

  @Override
  public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("query", querySpec));
    }
    QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
    return createSearchResult(translator.translate());
  }

  @Override
  public long count(QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("count", querySpec));
    }
    @SuppressWarnings("unchecked")
    AggregationQuery<T, Long> aggregationQuery =
        (AggregationQuery<T, Long>) createAggregationQuery(COUNT, dt, null, null, querySpec);
    return aggregationQuery.getResult().longValue();
  }

  @SuppressWarnings("unchecked")
  @Override
  public long countDistinctValues(String forField, QuerySpec querySpec)
      throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("countDistinctValues", forField, querySpec));
    }
    AggregationQuery<T, Long> aggregationQuery =
        (AggregationQuery<T, Long>) createAggregationQuery(COUNT_DISTINCT_VALUES, dt, forField,
            null, querySpec);
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
  public List<Map<String, Object>> getDistinctValuesPerGroup(String forGroup, String forField,
      QuerySpec querySpec) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("getDistinctValuesPerGroup", forField, forGroup, querySpec));
    }
    AggregationQuery<T, List<Map<String, Object>>> aggregationQuery =
        (AggregationQuery<T, List<Map<String, Object>>>) createAggregationQuery(
            GET_DISTINCT_VALUES_PER_GROUP, dt, forField, forGroup, querySpec);
    return aggregationQuery.getResult();
  }

  public String save(T apiObject, boolean immediate) {
    String id = apiObject.getId();
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    if (logger.isDebugEnabled()) {
      String pattern = "New save request (index={};type={};id={})";
      logger.debug(pattern, index, type, id);
    }
    IndexRequestBuilder request = ESUtil.esClient().prepareIndex(index, type, id);
    byte[] source = JsonUtil.serialize(apiObject);
    request.setSource(source);
    IndexResponse response = request.execute().actionGet();
    if (immediate) {
      IndicesAdminClient iac = ESUtil.esClient().admin().indices();
      RefreshRequestBuilder rrb = iac.prepareRefresh(index);
      rrb.execute().actionGet();
    }
    apiObject.setId(response.getId());
    return response.getId();
  }

  public boolean delete(String id, boolean immediate) {
    String index = dt.getIndexInfo().getName();
    String type = dt.getName();
    DeleteRequestBuilder request = ESUtil.esClient().prepareDelete(index, type, id);
    DeleteResponse response = request.execute().actionGet();
    if (immediate) {
      IndicesAdminClient iac = ESUtil.esClient().admin().indices();
      RefreshRequestBuilder rrb = iac.prepareRefresh(index);
      rrb.execute().actionGet();
    }
    return response.getResult() == Result.DELETED;
  }
  
  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
  {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("downloadQuery", querySpec, out));
    }
    querySpec.setSize(100);
    DirtyDocumentIterator<T> iterator = new DirtyDocumentIterator<>(dt, querySpec);
    
    Writer writer = new BufferedWriter(new OutputStreamWriter(out));
    while (iterator.hasNext()) {
      try {
        writer.write( JsonUtil.toPrettyJson(iterator.next()) );
        writer.flush();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
  }

  abstract T[] createDocumentObjectArray(int length);

  T[] processSearchRequest(SearchRequestBuilder request) {
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

  private QueryResult<T> createSearchResult(SearchRequestBuilder request) {
    SearchResponse response = executeSearchRequest(request);
    QueryResult<T> result = new QueryResult<>();
    result.setTotalSize(response.getHits().totalHits());
    result.setResultSet(createItems(response));
    return result;
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
