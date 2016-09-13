package nl.naturalis.nba.client;

import java.util.zip.ZipOutputStream;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;


public class TaxonClient extends AbstractClient implements ITaxonAccess {

	public TaxonClient(ClientConfig config)
	{
		super(config);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws NoSuchDataSetException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
