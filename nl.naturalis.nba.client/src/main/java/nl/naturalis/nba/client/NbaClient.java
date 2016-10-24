package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getQueryResult;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;
import static org.domainobject.util.http.SimpleHttpRequest.MIMETYPE_JSON;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpException;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpRequest;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * Abstract base class for all client-side implementations of the NBA API.
 * Provides HTTP request plumbing for subclasses.
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
		TypeReference<T> typeRef = new TypeReference<T>() {};
		return getObject(request.getResponseBody(), typeRef);
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
		TypeReference<T[]> typeRef = new TypeReference<T[]>() {};
		return getObject(request.getResponseBody(), typeRef);
	}

	@Override
	public QueryResult<T> query(QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpGet request = newJsonGetRequest();
		request.setPath(rootPath + "query");
		request.addParam("_querySpec", toJson(querySpec));
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		TypeReference<QueryResult<T>> typeRef = new TypeReference<QueryResult<T>>() {};
		return getQueryResult(request.getResponseBody(), typeRef);
	}

	@Override
	public QueryResult<Map<String, Object>> queryRaw(QuerySpec querySpec)
			throws InvalidQueryException
	{
		SimpleHttpGet request = newJsonGetRequest();
		request.setPath(rootPath + "queryRaw");
		request.addParam("_querySpec", toJson(querySpec));
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		TypeReference<QueryResult<Map<String, Object>>> typeRef = new TypeReference<QueryResult<Map<String, Object>>>() {};
		return getQueryResult(request.getResponseBody(), typeRef);
	}

	SimpleHttpGet newJsonGetRequest()
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setAccept(MIMETYPE_JSON);
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
