package nl.naturalis.nba.dao;

import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.elasticsearch.client.Client;
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
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.query.QuerySpecTranslator;

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
			logger.debug("find(\"{}\")", id);
		}
		GetRequestBuilder request = client().prepareGet();
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
			logger.debug("find({})", toJson(ids));
		}
		String type = dt.getName();
		SearchRequestBuilder request = newSearchRequest();
		IdsQueryBuilder query = QueryBuilders.idsQuery(type);
		query.ids(ids);
		request.setQuery(query);
		return processSearchRequest(request);
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
		return createQueryResult(translator.translate());
	}

	@Override
	public QueryResult<Map<String, Object>> queryData(QuerySpec spec) throws InvalidQueryException
	{
		QuerySpecTranslator translator = new QuerySpecTranslator(spec, dt);
		SearchRequestBuilder request = translator.translate();
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		if (logger.isDebugEnabled()) {
			logger.debug("Documents found: {}", response.getHits().totalHits());
		}
		List<Map<String, Object>> resultSet = new ArrayList<>(hits.length);
		if (spec.getFields() != null && spec.getFields().contains("id")) {
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

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		SearchRequestBuilder request;
		if (querySpec == null) {
			request = newSearchRequest();
		}
		else {
			QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
			request = translator.translate();
		}
		request.setSize(0);
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		return response.getHits().totalHits();
	}

	@Override
	public List<KeyValuePair<String, Long>> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug("getDistinctValues(\"{}\")", forField);
		}
		SearchRequestBuilder request;
		if (querySpec == null) {
			request = newSearchRequest();
		}
		else {
			QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
			request = translator.translate();
		}
		TermsBuilder termsBuilder = AggregationBuilders.terms(forField);
		termsBuilder.field(forField);
		request.setSize(0);
		request.addAggregation(termsBuilder);
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		if (logger.isDebugEnabled()) {
			logger.debug("Documents found: {}", response.getHits().totalHits());
		}
		Terms terms = response.getAggregations().get(forField);
		List<Bucket> buckets = terms.getBuckets();
		List<KeyValuePair<String, Long>> result = new ArrayList<>(buckets.size());
		for (Bucket bucket : buckets) {
			KeyValuePair<String, Long> kv = new KeyValuePair<String, Long>();
			kv.setKey(bucket.getKeyAsString());
			kv.setValue(bucket.getDocCount());
			result.add(kv);
		}
		return result;
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
		IndexRequestBuilder request = client().prepareIndex(index, type, id);
		byte[] source = JsonUtil.serialize(apiObject);
		request.setSource(source);
		IndexResponse response = request.execute().actionGet();
		if (immediate) {
			IndicesAdminClient iac = client().admin().indices();
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
		DeleteRequestBuilder request = client().prepareDelete(index, type, id);
		DeleteResponse response = request.execute().actionGet();
		return response.isFound();
	}

	abstract T[] createDocumentObjectArray(int length);

	SearchRequestBuilder newSearchRequest()
	{
		String index = dt.getIndexInfo().getName();
		String type = dt.getName();
		if (logger.isDebugEnabled()) {
			String pattern = "New search request (index={};type={})";
			logger.debug(pattern, index, type);
		}
		SearchRequestBuilder request = client().prepareSearch(index);
		request.setTypes(type);
		return request;
	}

	QueryResult<T> createQueryResult(SearchRequestBuilder request)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		QueryResult<T> result = new QueryResult<>();
		result.setTotalSize(response.getHits().totalHits());
		T[] apiObjects = processResponse(response);
		result.setResultSet(Arrays.asList(apiObjects));
		return result;
	}

	T[] processSearchRequest(SearchRequestBuilder request)
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Executing query:\n{}", request);
		}
		SearchResponse response = request.execute().actionGet();
		return processResponse(response);
	}

	T[] processResponse(SearchResponse response)
	{
		SearchHit[] hits = response.getHits().getHits();
		T[] apiObjects = createDocumentObjectArray(hits.length);
		for (int i = 0; i < hits.length; ++i) {
			String id = hits[i].getId();
			Map<String, Object> data = hits[i].getSource();
			T specimen = createDocumentObject(id, data);
			apiObjects[i] = specimen;
		}
		return apiObjects;
	}

	T createDocumentObject(String id, Map<String, Object> data)
	{
		ObjectMapper om = dt.getObjectMapper();
		T documentObject = om.convertValue(data, dt.getJavaType());
		documentObject.setId(id);
		return documentObject;
	}

	static Client client()
	{
		return ESClientManager.getInstance().getClient();
	}
}
