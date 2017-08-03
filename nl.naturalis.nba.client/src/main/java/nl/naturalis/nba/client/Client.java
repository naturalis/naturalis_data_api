package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.CT_APPLICATION_JSON;

import nl.naturalis.nba.utils.http.SimpleHttpGet;

abstract class Client {

	final ClientConfig config;
	final String rootPath;

	Client(ClientConfig config, String rootPath)
	{
		this.config = config;
		this.rootPath = rootPath;
	}

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

}