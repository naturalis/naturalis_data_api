package nl.naturalis.nba.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.naturalis.nba.api.INbaAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;


public class NbaClient2<DOCUMENT_OBJECT extends IDocumentObject> implements INbaAccess<DOCUMENT_OBJECT> {

	@Override
	public DOCUMENT_OBJECT find(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DOCUMENT_OBJECT[] find(String[] ids)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResult<DOCUMENT_OBJECT> query(QuerySpec querySpec) throws InvalidQueryException
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
	public Map<String, Long> getDistinctValues(String forField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Object, Set<Object>> getDistinctValuesPerGroup(String groupField, String valuesField,
			QueryCondition... conditions) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<KeyValuePair<Object, Integer>> getGroups(String groupByField, QuerySpec querySpec)
			throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
