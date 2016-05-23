package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ServerException.newServerException;
import static org.domainobject.util.http.SimpleHttpRequest.*;

import java.io.UnsupportedEncodingException;

import org.domainobject.util.http.SimpleHttpGet;

public class NBASession {

	private final ClientConfig cfg;

	NBASession(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	public SpecimenClient getSpecimenClient()
	{
		return new SpecimenClient(cfg);
	}

	public MultiMediaClient getMultiMediaObjectClient()
	{
		return new MultiMediaClient(cfg);
	}

	public TaxonClient getTaxonClient()
	{
		return new TaxonClient(cfg);
	}

	public String ping()
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(cfg.getBaseUrl());
		request.setAccept(MIMETYPE_JSON);
		request.setPath("/ping");
		AbstractClient.sendRequest(request);
		int status = request.getStatus();
		if (status == HTTP_NOT_FOUND) {
			return "Received a 404 (NOT FOUND) error. Please check Base URL: " + cfg.getBaseUrl();
		}
		else if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		try {
			return new String(request.getResponseBody(), "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
