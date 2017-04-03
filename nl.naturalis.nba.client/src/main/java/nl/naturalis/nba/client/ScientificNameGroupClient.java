package nl.naturalis.nba.client;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IScientificNameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NameGroupQuerySpec;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.ScientificNameGroup;

public class ScientificNameGroupClient extends NbaClient<ScientificNameGroup> implements IScientificNameGroupAccess {

	ScientificNameGroupClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}


	@Override
	public QueryResult<ScientificNameGroup> query(NameGroupQuerySpec querySpec)
			throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResult<ScientificNameGroup> getSpeciesWithSpecimens(NameGroupQuerySpec querySpec)
			throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
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
