package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;

import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class TaxonDao implements ITaxonAccess {

	private static Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
	{
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws InvalidQueryException
	{
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		return null;
	}

}
