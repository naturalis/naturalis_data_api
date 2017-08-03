package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getQueryResult;
import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.CT_APPLICATION_JSON;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.utils.ArrayUtil;
import nl.naturalis.nba.utils.http.SimpleHttpGet;
import nl.naturalis.nba.utils.http.SimpleHttpPost;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Abstract base class for all client-side implementations of the NBA API.
 * Implements shared functionality specified by {@link INbaAccess} and provides
 * HTTP request plumbing functionality for subclasses.
 * 
 * @author Ayco Holleman
 * @Author Tom Gilissen
 *
 */
abstract class NbaClient<T extends IDocumentObject> extends Client implements INbaAccess<T> {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NbaClient.class);

	NbaClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
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
	public T[] findByIds(String[] ids)
	{
		String imploded = ArrayUtil.implode(ids);
		SimpleHttpGet request = getJson(rootPath + "findByIds/" + imploded);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), documentObjectArrayClass());
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpRequest request;
		if (config.isPreferGET()) {
			request = new SimpleHttpGet();
			request.addQueryParam("_querySpec", toJson(querySpec));
		}
		else {
			request = new SimpleHttpPost();
			request.setRequestBody(toJson(querySpec), CT_APPLICATION_JSON);
		}
		request.setAccept(CT_APPLICATION_JSON);
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + "query");
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getQueryResult(request.getResponseBody(), queryResultTypeReference());
	}

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpPost request = new SimpleHttpPost();
		request.setAccept(CT_APPLICATION_JSON);
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + "count");
		request.setRequestBody(toJson(querySpec), CT_APPLICATION_JSON);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return ClientUtil.getObject(request.getResponseBody(), Long.class);
	}

	@Override
	public Map<String, Long> getDistinctValues(String forField, QuerySpec spec)
			throws InvalidQueryException
	{
		// TODO: implement
		return null;
	}

	abstract Class<T> documentObjectClass();

	abstract Class<T[]> documentObjectArrayClass();

	abstract TypeReference<QueryResult<T>> queryResultTypeReference();

}
