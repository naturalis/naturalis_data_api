package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.UnsupportedEncodingException;

import nl.naturalis.nba.api.IDocumentMetaData;
import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.utils.http.SimpleHttpGet;

/**
 * <p>
 * An {@code NbaSession} represents a connection to an NBA server and functions
 * as a factory for clients accessing different parts of the NBA REST API.
 * {@code NBASession} instances are light-weight objects and don't actually set
 * up or hold on to an HTTP connection. A typical workflow would look like this:
 * 
 * <pre>
 * 
 * NbaSession session = new NbaSession();
 * SpecimenClient client = session.getSpecimenClient();
 * Specimen specimen = client.findByUnitID("ZMA.RMNH.12345");
 * System.out.println("Record basis for specimen ZMA.RMNH.12345: " + specimen.getRecordBasis());
 * </pre>
 * </p>
 * <p>
 * Here is a more interesting example:
 * 
 * <pre>
 * NbaSession session = new NbaSession();
 * TaxonClient client = session.getTaxonClient();
 * QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
 * condition.and("acceptedName.specificEpithet", "=", "fuscus");
 * QuerySpec query = new QuerySpec();
 * query.addCondition(condition);
 * Taxon[] taxa = client.query(query);
 * for (Taxon taxon : taxa) {
 * 	System.out.println("Taxon id: " + taxon.getId());
 * }
 * </pre>
 * </p>
 * <p>
 * Here is how you can download that same query as a DarwinCore archive:
 * 
 * <pre>
 * TaxonClient client = session.getTaxonClient();
 * QueryCondition condition = new QueryCondition("acceptedName.genusOrMonomial", "=", "Larus");
 * condition.and("acceptedName.specificEpithet", "=", "Fuscus");
 * QuerySpec query = new QuerySpec();
 * query.addCondition(condition);
 * FileOutputStream fos = new FileOutputStream("C:/tmp/my-dwca.zip");
 * client.dwcaQuery(query, fos);
 * fos.close();
 * </pre>
 * </p>
 * <p>
 * And here is how you can download all pre-defined specimen datasets:
 * 
 * <pre>
 * NbaSession session = new NbaSession();
 * SpecimenClient client = session.getSpecimenClient();
 * for(String dataset : client.dwcaGetDataSetNames()) {
 * 	FileOutputStream fos = new FileOutputStream("C:/tmp/" + dataset + ".zip");
 * 	client.dwcaGetDataSet(dataset, fos);
 * 	fos.close();
 * }
 * </pre>
 * </p>
 * 
 * @author Ayco Holleman
 *
 */
public class NbaSession {

	private ClientConfig cfg;

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
	 * Returns a client providing access to specimen-related information. See
	 * {@link ISpecimenAccess}.
	 * 
	 * @return
	 */
	public SpecimenClient getSpecimenClient()
	{
		return new SpecimenClient(cfg, "specimen/");
	}

	/**
	 * Returns a client providing information about the NBA's {@link Specimen}
	 * index. See {@link IDocumentMetaData}.
	 * 
	 * @return
	 */
	public SpecimenMetaDataClient getSpecimenMetaDataClient()
	{
		return new SpecimenMetaDataClient(cfg, "specimen/metadata/");
	}

	/**
	 * Returns a client providing access to species-related information. See
	 * {@link ITaxonAccess}.
	 * 
	 * @return
	 */
	public TaxonClient getTaxonClient()
	{
		return new TaxonClient(cfg, "taxon/");
	}

	/**
	 * Returns a client providing informationabout the NBA's {@link Taxon}
	 * index. See {@link IDocumentMetaData}.
	 * 
	 * @return
	 */
	public TaxonMetaDataClient getTaxonMetaDataClient()
	{
		return new TaxonMetaDataClient(cfg, "taxon/metadata/");
	}

	/**
	 * Returns a client providing access to multimedia-related information. See
	 * {@link IMultiMediaObjectAccess}.
	 * 
	 * @return
	 */
	public MultiMediaObjectClient getMultiMediaObjectClient()
	{
		return new MultiMediaObjectClient(cfg, "multimedia/");
	}

	/**
	 * Returns a client providing information about the NBA's
	 * {@link MultiMediaObject} index. See {@link IDocumentMetaData}.
	 * 
	 * @return
	 */
	public MultiMediaObjectMetaDataClient getMultiMediaObjectMetaDataClient()
	{
		return new MultiMediaObjectMetaDataClient(cfg, "multimedia/metadata/");
	}

	/**
	 * Returns a client providing access to location-related information. See
	 * {@link IGeoAreaAccess}.
	 * 
	 * @return
	 */
	public GeoAreaClient getGeoAreaClient()
	{
		return new GeoAreaClient(cfg, "geo/");
	}

	/**
	 * Returns a client providing information about the NBA's {@link GeoArea}
	 * index. See {@link IDocumentMetaData}.
	 * 
	 * @return
	 */
	public GeoAreaMetaDataClient getGeoAreaMetaDataClient()
	{
		return new GeoAreaMetaDataClient(cfg, "geo/metadata/");
	}

	/**
	 * Returns a client providing access to NBA-wide metadata. See
	 * {@link INbaMetaData}.
	 * 
	 * @return
	 */
	public NbaMetaDataClient getNbaMetaDataClient()
	{
		return new NbaMetaDataClient(cfg, "metadata/");
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
		sendRequest(request);
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
