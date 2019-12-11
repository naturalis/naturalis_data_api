package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.invalidQueryException;
import static nl.naturalis.nba.client.ClientUtil.sendRequest;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.utils.IOUtil;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Provides access to location-related information. Client-side implementation
 * of the {@link IGeoAreaAccess}.
 * 
 * @see NbaSession
 * 
 * @author Ayco Holleman
 *
 */
public class GeoAreaClient extends NbaClient<GeoArea> implements IGeoAreaAccess {
  
  private static final Logger logger = LogManager.getLogger(GeoAreaClient.class);

	GeoAreaClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public GeoJsonObject getGeoJsonForLocality(String id)
	{
		SimpleHttpRequest request = getJson("getGeoJsonForLocality/" + id);
		int status = request.getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), GeoJsonObject.class);
	}

	@Override
	Class<GeoArea> documentObjectClass()
	{
		return GeoArea.class;
	}

	@Override
	Class<GeoArea[]> documentObjectArrayClass()
	{
		return GeoArea[].class;
	}

	@Override
	TypeReference<QueryResult<GeoArea>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<GeoArea>>() {};
	}

  @Override
  public void downloadQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException {
    Objects.requireNonNull(out, "Outputstream must not be Null");
    SimpleHttpRequest request = newQuerySpecRequest("download/", querySpec);
    request.setHeader("Accept-Encoding", "gzip");
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
      logger.info("Downloading result");
      in = request.getResponseBodyAsStream();
      IOUtil.pipe(in, out, 4096);
      logger.info("Download complete");
    } finally {
      IOUtil.close(in);
    }
  }

}
