package nl.naturalis.nba.client;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpRequest;

abstract class AbstractClient {

	private static final Logger logger = LogManager.getLogger(AbstractClient.class);

	protected final ClientConfig config;
	protected final SimpleHttpGet request;

	AbstractClient(ClientConfig config)
	{
		this.config = config;
		request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
	}

	void setPath(String path)
	{
		request.setPath(path);
	}

	SimpleHttpGet sendGETRequest()
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
		logger.info("Sending request: {}", uri);
		request.execute();
		return request;
	}

}
