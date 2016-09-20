package nl.naturalis.nba.client;

import static org.domainobject.util.http.SimpleHttpRequest.MIMETYPE_JSON;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpException;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpRequest;

abstract class AbstractClient {

	private static final Logger logger = LogManager.getLogger(AbstractClient.class);

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
		}
		return request;
	}

	protected final ClientConfig config;

	AbstractClient(ClientConfig config)
	{
		this.config = config;
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
