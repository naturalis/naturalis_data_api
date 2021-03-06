package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.CT_APPLICATION_JSON;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.utils.http.SimpleHttpGet;
import nl.naturalis.nba.utils.http.SimpleHttpPost;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

abstract class Client {

	private ClientConfig config;
	private String rootPath;

	Client(ClientConfig config, String rootPath)
	{
		this.config = config;
		this.rootPath = rootPath;
	}

	SimpleHttpRequest getJson(String path)
	{
		SimpleHttpRequest request = newGetRequest(path);
		request.setAccept(CT_APPLICATION_JSON);
		return sendRequest(request);
	}

	SimpleHttpRequest newQuerySpecRequest(String path, QuerySpec querySpec)
	{
		SimpleHttpRequest request;
		if (config.isPreferGET()) {
			request = newGetRequest(path);
			if (querySpec != null) {
				request.addQueryParam("_querySpec", toJson(querySpec));
			}
		}
		else {
			request = newPostRequest(path);
			if (querySpec != null) {
				request.setRequestBody(toJson(querySpec), CT_APPLICATION_JSON);
			}
		}
		return request;
	}

	SimpleHttpGet newGetRequest(String path)
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + path);
		return request;
	}

	SimpleHttpPost newPostRequest(String path)
	{
		SimpleHttpPost request = new SimpleHttpPost();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + path);
		return request;
	}

}