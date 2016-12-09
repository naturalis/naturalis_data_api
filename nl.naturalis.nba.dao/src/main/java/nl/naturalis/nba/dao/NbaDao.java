package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.Logger;
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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.csv.CsvWriter;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;
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
		return createDocumentObject(id, data);
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
		query.ids(ids);
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
		return createQueryResult(translator.translate());
	}

	@Override
	public QueryResult<Map<String, Object>> queryData(QuerySpec querySpec)
			throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("queryData", querySpec));
		}
		QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
		SearchRequestBuilder request = translator.translate();
		SearchResponse response = executeSearchRequest(request);
		SearchHit[] hits = response.getHits().getHits();
		List<Map<String, Object>> resultSet = new ArrayList<>(hits.length);
		if (querySpec.getFields() != null && querySpec.getFields().contains("id")) {
			for (SearchHit hit : hits) {
				Map<String, Object> source = hit.getSource();
				source.put("id", hit.getId());
				resultSet.add(hit.getSource());
			}
		}
		else {
			for (SearchHit hit : hits) {
				resultSet.add(hit.getSource());
			}
		}
		QueryResult<Map<String, Object>> result = new QueryResult<>();
		result.setTotalSize(response.getHits().totalHits());
		result.setResultSet(resultSet);
		return result;
	}

	public void csvQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("csvQuery", querySpec, out));
		}
		CsvWriter<T> writer = new CsvWriter<>(out, dt);
		writer.writeCsv(querySpec);
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
			QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
			request = translator.translate();
		}
		TermsBuilder termsBuilder = AggregationBuilders.terms(forField);
		termsBuilder.field(forField);
		request.setSize(0);
		request.addAggregation(termsBuilder);
		SearchResponse response = executeSearchRequest(request);
		Terms terms = response.getAggregations().get(forField);
		List<Bucket> buckets = terms.getBuckets();
		Map<String, Long> result = new TreeMap<>();
		for (Bucket bucket : buckets) {
			result.put(bucket.getKeyAsString(), bucket.getDocCount());
		}
		return result;
	}

	@Override
	public Map<Object, Set<Object>> getDistinctValuesPerGroup(String keyField, String valuesField,
			Condition... conditions) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("getDistinctValuesPerGroup", keyField, valuesField, conditions));
		}
		DistinctValuesPerGroupSearchHitHandler handler;
		handler = new DistinctValuesPerGroupSearchHitHandler(keyField, valuesField);
		QuerySpec qs = new QuerySpec();
		qs.setFields(Arrays.asList(keyField, valuesField));
		if (conditions != null && conditions.length != 0) {
			qs.setConditions(Arrays.asList(conditions));
		}
		qs.addCondition(new Condition(keyField, "!=", null));
		qs.addCondition(new Condition(valuesField, "!=", null));
		QuerySpecTranslator translator = new QuerySpecTranslator(qs, dt);
		SearchRequestBuilder request = translator.translate();
		Scroller scroller = new Scroller(request, handler);
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
		return response.isFound();
	}

	abstract T[] createDocumentObjectArray(int length);

	T[] processSearchRequest(SearchRequestBuilder request)
	{
		SearchResponse response = executeSearchRequest(request);
		return processSearchResponse(response);
	}

	private QueryResult<T> createQueryResult(SearchRequestBuilder request)
	{
		SearchResponse response = executeSearchRequest(request);
		QueryResult<T> result = new QueryResult<>();
		result.setTotalSize(response.getHits().totalHits());
		T[] documentObjects = processSearchResponse(response);
		result.setResultSet(Arrays.asList(documentObjects));
		return result;
	}

	private T[] processSearchResponse(SearchResponse response)
	{
		SearchHit[] hits = response.getHits().getHits();
		T[] documentObjects = createDocumentObjectArray(hits.length);
		for (int i = 0; i < hits.length; ++i) {
			String id = hits[i].getId();
			Map<String, Object> data = hits[i].getSource();
			documentObjects[i] = createDocumentObject(id, data);
		}
		return documentObjects;
	}

	private T createDocumentObject(String id, Map<String, Object> data)
	{
		ObjectMapper om = dt.getObjectMapper();
		T documentObject = om.convertValue(data, dt.getJavaType());
		documentObject.setId(id);
		return documentObject;
	}

}
