package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.UnsupportedEncodingException;

import nl.naturalis.nba.utils.http.SimpleHttpGet;

/**
 * An {@code NBASession} represents a connection to an NBA server instance and
 * functions as a factory for clients accessing different parts of the NBA's
 * data store (e&#46;g&#46; a {@link SpecimenClient client} accessing
 * specimen-related data). {@code NBASession} instances are light-weight objects
 * and don't actually set up or hold on to an HTTP connection. A typical
 * workflow would look like this:<br>
 * <code>
 * NBASession session = new NBASession();
 * SpecimenClient client = session.getSpecimenClient();
 * Specimen specimen = client.findByUnitID("ZMA.RMNH.12345");
 * </code><br>
 * 
 * 
 * @author Ayco Holleman
 *
 */
public class NbaSession {

	private final ClientConfig cfg;

	/**
	 * Sets up a session that will connect to the production version of the NBA.
	 */
	public NbaSession()
	{
		this.cfg = new ClientConfig();
	}

	/**
	 * Sets up a session using the specified {@link ClientConfig client
	 * configuration}.
	 * 
	 * @param cfg
	 */
	public NbaSession(ClientConfig cfg)
	{
		this.cfg = cfg;
	}

	/**
	 * Returns a client for specimen-related data.
	 * 
	 * @return
	 */
	public SpecimenClient getSpecimenClient()
	{
		return new SpecimenClient(cfg, "specimen/");
	}

	/**
	 * Returns a client for multimedia-related data.
	 * 
	 * @return
	 */
	public MultiMediaObjectClient getMultiMediaObjectClient()
	{
		return new MultiMediaObjectClient(cfg, "multimedia/");
	}

	/**
	 * Returns a client for taxon-related data.
	 * 
	 * @return
	 */
	public TaxonClient getTaxonClient()
	{
		return new TaxonClient(cfg, "taxon/");
	}

	/**
	 * Returns a client for taxon-related data.
	 * 
	 * @return
	 */
	public GeoAreaClient getGeoAreaClient()
	{
		return new GeoAreaClient(cfg, "geo/");
	}

	/**
	 * Tests whether this is a valid session by calling a simple &#34;ping&#34;
	 * service.
	 * 
	 * @return The message coming back from the ping service.
	 */
	public String ping()
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(cfg.getBaseUrl());
		request.setAccept("text/plain");
		request.setPath("/ping");
		NbaClient.sendRequest(request);
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
