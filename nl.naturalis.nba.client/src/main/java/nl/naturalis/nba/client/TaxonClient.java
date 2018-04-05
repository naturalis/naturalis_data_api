package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.invalidQueryException;
import static nl.naturalis.nba.client.ClientUtil.noSuchDataSetException;
import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import nl.naturalis.nba.api.GroupByScientificNameQueryResult;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Provides access to taxon-related information. Client-side implementation of
 * the {@link ITaxonAccess}.
 * 
 * @see NbaSession
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
		SimpleHttpRequest request = newQuerySpecRequest("dwca/query", querySpec);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			byte[] response = request.getResponseBody();
			ServerException exception = newServerException(status, response);
			if (exception.was(InvalidQueryException.class)) {
				throw invalidQueryException(exception);
			}
			throw exception;
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
		SimpleHttpRequest request = newGetRequest("dwca/getDataSet/" + name);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			byte[] response = request.getResponseBody();
			ServerException exception = newServerException(status, response);
			if (exception.was(NoSuchDataSetException.class)) {
				throw noSuchDataSetException(exception);
			}
			throw exception;
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
		SimpleHttpRequest request = getJson("dwca/getDataSetNames");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public GroupByScientificNameQueryResult groupByScientificName(
			GroupByScientificNameQuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpRequest request = newQuerySpecRequest("groupByScientificName", querySpec);
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			byte[] response = request.getResponseBody();
			ServerException exception = newServerException(status, response);
			if (exception.was(InvalidQueryException.class)) {
				throw invalidQueryException(exception);
			}
			throw exception;
		}
		return getObject(request.getResponseBody(), GroupByScientificNameQueryResult.class);
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
  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException {
    // TODO Auto-generated method stub
    
  }

}
