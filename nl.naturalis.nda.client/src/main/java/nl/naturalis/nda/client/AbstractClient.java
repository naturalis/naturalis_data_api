package nl.naturalis.nda.client;

import org.domainobject.util.http.SimpleHttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractClient {

	private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

	protected final ClientConfig config;
	protected final SimpleHttpGet request;


	AbstractClient(ClientConfig config)
	{
		this.config = config;
		request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
	}


	protected void setPath(String path)
	{
		String base = config.getBaseUrl();
		if (base.charAt(base.length() - 1) != '/' && path.charAt(0) != '/') {
			path = '/' + path;
		}
		logger.info("Calling NBA: " + base + path);
		request.setPath(path);
	}

}
