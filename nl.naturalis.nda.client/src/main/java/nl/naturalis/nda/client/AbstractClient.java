package nl.naturalis.nda.client;

import org.domainobject.util.http.SimpleHttpGet;

abstract class AbstractClient {

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
		request.setPath(path);
	}

}
