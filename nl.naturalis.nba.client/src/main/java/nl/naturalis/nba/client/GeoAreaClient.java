package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.*;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.util.List;
import java.util.Map;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.utils.http.SimpleHttpGet;

public class GeoAreaClient extends NbaClient<GeoArea> implements IGeoAreaAccess {

	GeoAreaClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public GeoArea find(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoArea[] find(String[] ids)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResult<GeoArea> query(QuerySpec querySpec) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResult<Map<String, Object>> queryData(QuerySpec spec) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count(QuerySpec querySpec) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<KeyValuePair<String, Long>> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdForLocality(String locality)
	{
		SimpleHttpGet request = getJson("geo/getIdForLocality/" + locality);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getString(request.getResponseBody());
	}

	@Override
	public String getIdForIsoCode(String isoCode)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GeoJsonObject getGeoJsonForId(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<KeyValuePair<String, String>> getLocalities()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<KeyValuePair<String, String>> getIsoCodes()
	{
		// TODO Auto-generated method stub
		return null;
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

}
