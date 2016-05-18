package nl.naturalis.nba.client;

import static org.domainobject.util.http.SimpleHttpRequest.MIMETYPE_JSON;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpException;
import org.domainobject.util.http.SimpleHttpGet;
import org.domainobject.util.http.SimpleHttpPost;
import org.domainobject.util.http.SimpleHttpRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.common.json.ObjectMapperLocator;

abstract class AbstractClient {

	private static final Logger logger = LogManager.getLogger(AbstractClient.class);

	protected final ClientConfig config;
	protected final SimpleHttpGet GET;
	protected final SimpleHttpPost POST;

	AbstractClient(ClientConfig config)
	{
		this.config = config;
		GET = new SimpleHttpGet();
		GET.setBaseUrl(config.getBaseUrl());
		POST = new SimpleHttpPost();
		POST.setBaseUrl(config.getBaseUrl());
	}

	SimpleHttpGet setGETBody(Object obj)
	{
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		GET.setContentType(MIMETYPE_JSON);
		GET.setObject(obj, om);
		return GET;
	}

	SimpleHttpPost setPOSTBody(Object obj)
	{
		ObjectMapperLocator oml = ObjectMapperLocator.getInstance();
		ObjectMapper om = oml.getObjectMapper(obj.getClass());
		POST.setContentType("application/json");
		POST.setObject(obj, om);
		return POST;
	}

	SimpleHttpGet sendGETRequest()
	{
		URI uri = getURI(GET);
		logger.info("Sending GET request: {}", uri);
		GET.execute();
		return GET;
	}

	SimpleHttpPost sendPOSTRequest()
	{
		URI uri = getURI(POST);
		logger.info("Sending POST request: {}", uri);
		try {
			POST.execute();
		}
		catch (Throwable t) {
			if (t instanceof SimpleHttpException) {
				if (t.getMessage().indexOf("Connection refused") != -1) {
					String fmt = "NBA server down or invalid base URL: %s";
					String msg = String.format(fmt, POST.getBaseUrl());
					throw new ClientException(msg);
				}
			}
		}
		return POST;
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
