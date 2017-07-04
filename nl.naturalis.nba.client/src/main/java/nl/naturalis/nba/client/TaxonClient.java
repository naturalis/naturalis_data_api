package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup2;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.utils.http.SimpleHttpGet;

/**
 * Client-side implementation of the {@link ITaxonAccess taxon API}.
 * 
 * @author Ayco Holleman
 *
 */
public class TaxonClient extends NbaClient<Taxon> implements ITaxonAccess {

	private static final Logger logger = LogManager.getLogger(TaxonClient.class);

	TaxonClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		String json = JsonUtil.toJson(querySpec);
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath("taxon/dwca/query/" + json);
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
		request.setPath("taxon/dwca/dataset/" + name);
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
		SimpleHttpGet request = getJson("taxon/dwca/getDataSetNames");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	Class<Taxon> documentObjectClass()
	{
		return Taxon.class;
	}

	@Override
	Class<Taxon[]> documentObjectArrayClass()
	{
		return Taxon[].class;
	}

	@Override
	TypeReference<QueryResult<Taxon>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<Taxon>>() {};
	}

	@Override
	public QueryResult<ScientificNameGroup2> groupByScientificName(
			ScientificNameGroupQuerySpec querySpec) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
