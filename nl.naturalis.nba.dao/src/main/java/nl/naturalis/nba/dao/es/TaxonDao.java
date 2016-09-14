package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;

import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.es.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.es.format.dwca.DwcaWriter;

public class TaxonDao implements ITaxonAccess {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, ZipOutputStream out) throws InvalidQueryException
	{
	}

	@Override
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws NoSuchDataSetException
	{
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.TAXON);
			DwcaWriter writer = new DwcaWriter(config, out);
			writer.write();
		}
		catch (DataSetConfigurationException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		return null;
	}

}
