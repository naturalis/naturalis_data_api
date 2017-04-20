package nl.naturalis.nba.client;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;


public class SpecimenClient2 extends NbaClient2<Specimen> implements ISpecimenAccess {

	@Override
	public Specimen find(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Specimen[] find(String[] ids)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryResult<Specimen> query(QuerySpec querySpec) throws InvalidQueryException
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

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String unitID)
	{
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
//	{
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException
//	{
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNamedCollections()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getIdsInCollection(String collectionName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String save(Specimen specimen, boolean immediate)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(String id, boolean immediate)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		// TODO Auto-generated method stub
		
	}

}
