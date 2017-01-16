package nl.naturalis.nba.client;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.MultiMediaObject;

public class MultiMediaClient extends NbaClient<MultiMediaObject>
		implements IMultiMediaObjectAccess {

	MultiMediaClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

	@Override
	Class<MultiMediaObject> documentObjectClass()
	{
		return MultiMediaObject.class;
	}

	@Override
	Class<MultiMediaObject[]> documentObjectArrayClass()
	{
		return MultiMediaObject[].class;
	}

	@Override
	TypeReference<QueryResult<MultiMediaObject>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<MultiMediaObject>>() {};
	}

}
