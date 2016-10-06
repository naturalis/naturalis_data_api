package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getBoolean;
import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getString;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpGet;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

/**
 * Client-side implementation of the {@link ISpecimenAccess specimen API} defined
 * by the NBA.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenClient extends AbstractClient implements ISpecimenAccess {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenClient.class);

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}

	@Override
	public boolean exists(String unitID)
	{
		SimpleHttpGet request = getJson("specimen/exists/" + unitID);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getBoolean(request.getResponseBody());
	}

	@Override
	public Specimen find(String id)
	{
		SimpleHttpGet request = getJson("specimen/find/" + id);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen.class);
	}

	@Override
	public Specimen[] find(String[] ids)
	{
		String json = JsonUtil.toJson(ids);
		SimpleHttpGet request = getJson("specimen/findByIds/" + json);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen[].class);
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		SimpleHttpGet request = getJson("specimen/findByUnitID/" + unitID);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen[].class);
	}

	@Override
	public String[] getNamedCollections()
	{
		SimpleHttpGet request = getJson("specimen/getNamedCollections");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public String[] getIdsInCollection(String collectionName)
	{
		String url = "specimen/getIdsInCollection/" + collectionName;
		SimpleHttpGet request = getJson(url);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public Specimen[] query(QuerySpec querySpec) throws InvalidQueryException
	{
		String json = JsonUtil.toJson(querySpec);
		SimpleHttpGet request = getJson("specimen/query/" + json);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen[].class);
	}

	@Override
	public String save(Specimen specimen, boolean immediate)
	{
		String json = JsonUtil.toJson(specimen);
		SimpleHttpGet request;
		if (immediate) {
			request = getJson("specimen/save/immediate/" + json);
		}
		else {
			request = getJson("specimen/save/" + json);
		}
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getString(request.getResponseBody());
	}

	@Override
	public boolean delete(String id, boolean immediate)
	{
		return false;
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
