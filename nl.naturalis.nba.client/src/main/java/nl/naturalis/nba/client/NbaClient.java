package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getQueryResult;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.CT_APPLICATION_JSON;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.http.SimpleHttpException;
import nl.naturalis.nba.utils.http.SimpleHttpGet;
import nl.naturalis.nba.utils.http.SimpleHttpPost;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Abstract base class for all client-side implementations of the NBA API.
 * Implements shared functionality specified by {@link INbaAccess} and provides
 * HTTP request plumbing functionality for subclasses.
 * 
 * @author Ayco Holleman
 *
 */
abstract class NbaClient<T extends IDocumentObject> implements INbaAccess<T> {

	private static final Logger logger = LogManager.getLogger(NbaClient.class);

	static SimpleHttpRequest sendRequest(SimpleHttpRequest request)
	{
		URI uri = getURI(request);
		logger.info("Sending {} request:\n{}", request.getMethod(), uri);
		try {
			request.execute();
		}
		catch (Throwable t) {
			if (t instanceof SimpleHttpException) {
				if (t.getMessage().indexOf("Connection refused") != -1) {
					String fmt = "NBA server down or invalid base URL: %s";
					String msg = String.format(fmt, request.getBaseUrl());
					throw new ClientException(msg);
				}
			}
			throw t;
		}
		return request;
	}

	final ClientConfig config;
	final String rootPath;

	NbaClient(ClientConfig config, String rootPath)
	{
		this.config = config;
		this.rootPath = rootPath;
	}

	@Override
	public T find(String id)
	{
		SimpleHttpGet request = getJson(rootPath + "find/" + id);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), documentObjectClass());
	}

	@Override
	public T[] find(String[] ids)
	{
		String json = JsonUtil.toJson(ids);
		SimpleHttpGet request = getJson(rootPath + "findByIds/" + json);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), documentObjectArrayClass());
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpPost request = new SimpleHttpPost();
		request.setAccept(CT_APPLICATION_JSON);
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + "query");
		request.setRequestBody(toJson(querySpec), CT_APPLICATION_JSON);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getQueryResult(request.getResponseBody(), queryResultTypeReference());
	}

	@Override
	public QueryResult<Map<String, Object>> queryData(QuerySpec querySpec)
			throws InvalidQueryException
	{
		TypeReference<QueryResult<Map<String, Object>>> typeRef;
		typeRef = new TypeReference<QueryResult<Map<String, Object>>>() {};
		SimpleHttpPost request = new SimpleHttpPost();
		request.setAccept(CT_APPLICATION_JSON);
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + "queryData");
		request.setRequestBody(toJson(querySpec), CT_APPLICATION_JSON);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getQueryResult(request.getResponseBody(), typeRef);
	}

	@Override
	public void csvQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
	}

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		return 0;
	}

	@Override
	public Map<String, Long> getDistinctValues(String forField, QuerySpec spec)
			throws InvalidQueryException
	{
		return null;
	}

	@Override
	public Map<Object, Set<Object>> getDistinctValuesPerGroup(String groupField, String valuesField,
			QueryCondition... conditions) throws InvalidQueryException
	{
		return null;
	}

	@Override
	public List<KeyValuePair<Object, Integer>> getGroups(String groupByField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		return null;
	}

	abstract Class<T> documentObjectClass();

	abstract Class<T[]> documentObjectArrayClass();

	abstract TypeReference<QueryResult<T>> queryResultTypeReference();

	SimpleHttpGet newJsonGetRequest()
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setAccept(CT_APPLICATION_JSON);
		return request;
	}

	SimpleHttpGet getJson(String path)
	{
		SimpleHttpGet request = newJsonGetRequest();
		request.setPath(path);
		return (SimpleHttpGet) sendRequest(request);
	}

	private static URI getURI(SimpleHttpRequest request)
	{
		URI uri = null;
		try {
			uri = request.createUri();
		}
		catch (URISyntaxException e) {
			String fmt = "Invalid URL (path: \"%s\"; query: \"%s\")";
			String msg = String.format(fmt, request.getPath(), request.getQuery());
			throw new ClientException(msg);
		}
		return uri;
	}

}
