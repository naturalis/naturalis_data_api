package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;
import java.io.OutputStream;
import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.GeoArea;
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
    // TODO Auto-generated method stub
    
  }

}
