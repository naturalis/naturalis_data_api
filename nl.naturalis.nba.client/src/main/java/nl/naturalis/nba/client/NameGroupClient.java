package nl.naturalis.nba.client;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INameGroupAccess;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.NameGroup;

public class NameGroupClient extends NbaClient<NameGroup> implements INameGroupAccess {

	NameGroupClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

	@Override
	Class<NameGroup> documentObjectClass()
	{
		return NameGroup.class;
	}

	@Override
	Class<NameGroup[]> documentObjectArrayClass()
	{
		return NameGroup[].class;
	}

	@Override
	TypeReference<QueryResult<NameGroup>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<NameGroup>>() {};
	}

}
