package nl.naturalis.nba.client;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INameGroupAccess;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.ScientificNameGroup;

public class NameGroupClient extends NbaClient<ScientificNameGroup> implements INameGroupAccess {

	NameGroupClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

	@Override
	Class<ScientificNameGroup> documentObjectClass()
	{
		return ScientificNameGroup.class;
	}

	@Override
	Class<ScientificNameGroup[]> documentObjectArrayClass()
	{
		return ScientificNameGroup[].class;
	}

	@Override
	TypeReference<QueryResult<ScientificNameGroup>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<ScientificNameGroup>>() {};
	}

}
