package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getBoolean;
import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getString;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.common.json.JsonUtil.toJson;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.utils.http.SimpleHttpGet;

/**
 * Client-side implementation of the {@link ISpecimenAccess specimen API}.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenClient extends NbaClient<Specimen> implements ISpecimenAccess {

	private static final Logger logger = LogManager.getLogger(SpecimenClient.class);

	SpecimenClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
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
	public String save(Specimen specimen, boolean immediate)
	{
		String json = toJson(specimen);
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
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		String json = JsonUtil.toJson(querySpec);
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath("specimen/dwca/query/" + json);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		InputStream in = null;
		try {
			logger.info("Downloading DarwinCore archive");
			in = request.getResponseBodyAsStream();
			IOUtil.pipe(in, out, 4096);
			logger.info("DarwinCore archive download complete");
		}
		finally {
			IOUtil.close(in);
		}
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath("specimen/dwca/dataset/" + name);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		InputStream in = null;
		try {
			logger.info("Downloading DarwinCore archive");
			in = request.getResponseBodyAsStream();
			IOUtil.pipe(in, out, 4096);
			logger.info("DarwinCore archive download complete");
		}
		finally {
			IOUtil.close(in);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		SimpleHttpGet request = getJson("specimen/dwca/getDataSetNames");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public QueryResult<ScientificNameGroup> groupByScientificName(
			GroupByScientificNameQuerySpec querySpec) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Class<Specimen> documentObjectClass()
	{
		return Specimen.class;
	}

	@Override
	Class<Specimen[]> documentObjectArrayClass()
	{
		return Specimen[].class;
	}

	@Override
	TypeReference<QueryResult<Specimen>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<Specimen>>() {};
	}

}
