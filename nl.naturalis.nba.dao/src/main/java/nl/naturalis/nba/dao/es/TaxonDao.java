package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.es.DaoUtil.getLogger;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.exception.DaoException;
import nl.naturalis.nba.dao.es.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.es.format.DataSetWriteException;
import nl.naturalis.nba.dao.es.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.es.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.es.format.dwca.DwcaUtil;
import nl.naturalis.nba.dao.es.format.dwca.DwcaWriter;

public class TaxonDao implements ITaxonAccess {

	@SuppressWarnings("unused")
	private static Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		try {
			DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.TAXON);
			DwcaWriter writer = new DwcaWriter(config, out);
			writer.writeDwcaForQuery(querySpec);
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.TAXON);
			DwcaWriter writer = new DwcaWriter(config, out);
			writer.writeDwcaForDataSet();
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		File dir = DwcaUtil.getDwcaConfigurationDirectory(DwcaDataSetType.TAXON);
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.getName().startsWith("dynamic")) {
					return false;
				}
				if (f.isFile() && f.getName().endsWith(DwcaConfig.CONF_FILE_EXTENSION))
					return true;
				return false;
			}
		});
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			names[i] = name.substring(0, name.indexOf('.'));
		}
		return names;
	}

}
