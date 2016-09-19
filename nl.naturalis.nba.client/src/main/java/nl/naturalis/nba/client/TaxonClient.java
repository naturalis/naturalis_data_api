package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ServerException.newServerException;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.IOUtil;
import org.domainobject.util.http.SimpleHttpGet;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

public class TaxonClient extends AbstractClient implements ITaxonAccess {

	private static final Logger logger = LogManager.getLogger(TaxonClient.class);

	public TaxonClient(ClientConfig config)
	{
		super(config);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
	{
		String json = JsonUtil.toJson(querySpec);
		logger.info("Executing DwCA query:\n{}", json);
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath("taxon/dwca/query/" + json);
		request.execute();
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		logger.info("Downloading and saving DarwinCore archive");
		IOUtil.pipe(request.getResponseAsStream(), out, 4096);
		logger.info("DarwinCore archive download complete");
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws NoSuchDataSetException
	{
		logger.info("Retrieving DwCA data set \"{}\"", name);
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath("taxon/dwca/dataset/" + name);
		request.execute();
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		logger.info("Downloading and saving DarwinCore archive");
		IOUtil.pipe(request.getResponseAsStream(), out, 4096);
		logger.info("DarwinCore archive download complete");
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		return null;
	}

}
