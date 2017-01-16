package nl.naturalis.nba.client;

import org.geojson.GeoJsonObject;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IGeoAreaAccess;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.GeoArea;

public class GeoAreaClient extends NbaClient<GeoArea> implements IGeoAreaAccess {

	GeoAreaClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public GeoJsonObject getGeoJsonForLocality(String id)
	{
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
