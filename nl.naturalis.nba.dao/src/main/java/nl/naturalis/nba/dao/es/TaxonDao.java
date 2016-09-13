package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;

import java.io.File;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.FileUtil;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.DataSet;
import nl.naturalis.nba.dao.es.format.DataSetBuilder;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.csv.CsvFieldFactory;
import nl.naturalis.nba.dao.es.format.dwca.DwcaWriter;

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
	public void dwcaGetDataSet(String name, ZipOutputStream out) throws NoSuchDataSetException
	{
		File root = DaoRegistry.getInstance().getConfigurationDirectory();
		File confFile = FileUtil.newFile(root, "dwca/taxon/" + name + ".dataset-config.xml");
		logger.info("Searching for configuration file for data set \"{}\": {}",
				confFile.getAbsolutePath());
		if (!confFile.isFile()) {
			String msg = String.format("No such data set: \"%s\"", name);
			throw new DaoException(msg);
		}
		try {
			DataSetBuilder dsb = new DataSetBuilder(confFile);
			dsb.setDefaultFieldFactory(new CsvFieldFactory());
			DataSet dataSet = dsb.build();
			DwcaWriter writer = new DwcaWriter(dataSet, out);
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
