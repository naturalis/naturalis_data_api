package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import static org.elasticsearch.search.aggregations.AggregationBuilders.nested;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QueryResultItem;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortOrder;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.dao.util.es.Scroller;

abstract class NbaDao<T extends IDocumentObject> implements INbaAccess<T> {

	private static final Logger logger = getLogger(NbaDao.class);

	private final DocumentType<T> dt;

	NbaDao(DocumentType<T> dt)
	{
		this.dt = dt;
	}

	@Override
	public T find(String id)
	{
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
		Map<String, Object> data = response.getSource();
		return newDocumentObject(id, data);
	}

	@Override
	public T[] find(String[] ids)
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("find", ids));
		}
		String type = dt.getName();
		SearchRequestBuilder request = newSearchRequest(dt);
		IdsQueryBuilder query = QueryBuilders.idsQuery(type);
		query.addIds(ids);
		request.setQuery(query);
		return processSearchRequest(request);
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("query", querySpec));
		}
		QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
		return createSearchResult(translator.translate());
	}

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("count", querySpec));
		}
		SearchRequestBuilder request;
		if (querySpec == null) {
			request = newSearchRequest(dt);
		}
		else {
			QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
			request = translator.translate();
		}
		request.setSize(0);
		SearchResponse response = executeSearchRequest(request);
		return response.getHits().totalHits();
	}

	/*
	 * NB Paging is notoriously not possible for aggregations. See
	 * https://github.com/elastic/elasticsearch/issues/4915. Also note Byron's
	 * contribution/solution on that page. This solution is not practical for us
	 * since we often want to group on fields with very high cardinality (e.g.
	 * the scientific name field), where the number of groups is close to the
	 * total number of documents. In other words you might as well do a regular
	 * (non-aggregation) query and then collect the documents per group
	 * manually.
	 */
	@Override
	public List<KeyValuePair<Object, Integer>> getGroups(String groupByField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("group", groupByField, querySpec));
		}
		int from = 0;
		int size = 0;
		if (querySpec != null) {
			if (querySpec.getFrom() != null) {
				from = querySpec.getFrom();
				querySpec.setFrom(null);
			}
			if (querySpec.getSize() != null) {
				size = querySpec.getSize();
				querySpec.setSize(null);
			}
		}
		else {
			querySpec = new QuerySpec();
		}
		querySpec.addFields(groupByField);
		querySpec.addCondition(new QueryCondition(groupByField, NOT_EQUALS, null));
		querySpec.sortBy(groupByField);
		GetGroupsSearchHitHandler handler = new GetGroupsSearchHitHandler(groupByField, from, size);
		Scroller scroller = new Scroller(querySpec, dt, handler);
		try {
			scroller.scroll();
		}
		catch (NbaException e) {
			// Should not happen - handler does not throw exceptions
			throw new DaoException(e);
		}
		return handler.getGroups();
	}

	@Override
	public Map<String, Long> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getDistinctValues", forField, querySpec));
		}
		SearchRequestBuilder request;
		if (querySpec == null) {
			request = newSearchRequest(dt);
		}
		else {
			request = new QuerySpecTranslator(querySpec, dt).translate();
		}
		request.setSize(0);
		MappingInfo<T> mappingInfo = new MappingInfo<>(dt.getMapping());
		String nestedPath;
		try {
			nestedPath = mappingInfo.getNestedPath(forField);
		}
		catch (NoSuchFieldException e) {
			throw new InvalidQueryException(e.getMessage());
		}
		TermsAggregationBuilder termsAggregation = terms("agg0");
		termsAggregation.field(forField);
		termsAggregation.size(10000);
		Terms terms;
		if (nestedPath == null) {
			request.addAggregation(termsAggregation);
			SearchResponse response = executeSearchRequest(request);
			terms = response.getAggregations().get("agg0");
		}
		else {
			NestedAggregationBuilder nestedAggregation = nested("agg1", nestedPath);
			nestedAggregation.subAggregation(termsAggregation);
			request.addAggregation(nestedAggregation);
			SearchResponse response = executeSearchRequest(request);
			Nested nested = response.getAggregations().get("agg1");
			terms = nested.getAggregations().get("agg0");
		}
		Map<String, Long> result = new LinkedHashMap<>(terms.getBuckets().size());
		for (Bucket bucket : terms.getBuckets()) {
			result.put(bucket.getKeyAsString(), bucket.getDocCount());
		}
		return result;
	}

	@Override
	public Map<Object, Set<Object>> getDistinctValuesPerGroup(String keyField, String valuesField,
			QueryCondition... conditions) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getDistinctValuesPerGroup", keyField, valuesField, conditions));
		}
		DistinctValuesPerGroupSearchHitHandler handler;
		handler = new DistinctValuesPerGroupSearchHitHandler(keyField, valuesField);
		QuerySpec qs = new QuerySpec();
		qs.addFields(keyField, valuesField);
		if (conditions != null && conditions.length != 0) {
			qs.setConditions(Arrays.asList(conditions));
		}
		qs.addCondition(new QueryCondition(keyField, "!=", null));
		qs.addCondition(new QueryCondition(valuesField, "!=", null));
		qs.sortBy(keyField, SortOrder.DESC);
		Scroller scroller = new Scroller(qs, dt, handler);
		try {
			scroller.scroll();
		}
		catch (NbaException e) {
			throw (InvalidQueryException) e;
		}
		return handler.getDistinctValuesPerGroup();
	}

	public String save(T apiObject, boolean immediate)
	{
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

	public boolean delete(String id, boolean immediate)
	{
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

	abstract T[] createDocumentObjectArray(int length);

	T[] processSearchRequest(SearchRequestBuilder request)
	{
		SearchResponse response = executeSearchRequest(request);
		return processQueryResponse(response);
	}

	private QueryResult<T> createSearchResult(SearchRequestBuilder request)
	{
		SearchResponse response = executeSearchRequest(request);
		QueryResult<T> result = new QueryResult<>();
		result.setTotalSize(response.getHits().totalHits());
		result.setResultSet(createItems(response));
		return result;
	}

	private T[] processQueryResponse(SearchResponse response)
	{
		SearchHit[] hits = response.getHits().getHits();
		T[] documentObjects = createDocumentObjectArray(hits.length);
		for (int i = 0; i < hits.length; ++i) {
			String id = hits[i].getId();
			Map<String, Object> data = hits[i].getSource();
			documentObjects[i] = newDocumentObject(id, data);
		}
		return documentObjects;
	}

	private List<QueryResultItem<T>> createItems(SearchResponse response)
	{
		ObjectMapper om = dt.getObjectMapper();
		Class<T> type = dt.getJavaType();
		SearchHit[] hits = response.getHits().getHits();
		List<QueryResultItem<T>> items = new ArrayList<>(hits.length);
		for (SearchHit hit : hits) {
			T obj = om.convertValue(hit.getSource(), type);
			obj.setId(hit.getId());
			items.add(new QueryResultItem<T>(obj, hit.getScore()));
		}
		return items;
	}

	private T newDocumentObject(String id, Map<String, Object> data)
	{
		ObjectMapper om = dt.getObjectMapper();
		T obj = om.convertValue(data, dt.getJavaType());
		obj.setId(id);
		return obj;
	}

}
