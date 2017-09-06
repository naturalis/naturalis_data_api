package nl.naturalis.nba.dao;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import java.io.File;
import java.io.FileFilter;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.ITaxonAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.dwca.DwcaConfig;
import nl.naturalis.nba.dao.format.dwca.DwcaDataSetType;
import nl.naturalis.nba.dao.format.dwca.DwcaUtil;
import nl.naturalis.nba.dao.format.dwca.IDwcaWriter;
import nl.naturalis.nba.dao.util.GroupTaxaByScientificNameHelper;

public class TaxonDao extends NbaDao<Taxon> implements ITaxonAccess {

	private static final Logger logger = getLogger(TaxonDao.class);

	public TaxonDao()
	{
		super(TAXON);
	}

	@Override
	public void dwcaQuery(QuerySpec querySpec, OutputStream out) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaQuery", querySpec, out));
		}
		try {
			DwcaConfig config = DwcaConfig.getDynamicDwcaConfig(DwcaDataSetType.TAXON);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForQuery(querySpec);
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public void dwcaGetDataSet(String name, OutputStream out) throws NoSuchDataSetException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSet", name, out));
		}
		try {
			DwcaConfig config = new DwcaConfig(name, DwcaDataSetType.TAXON);
			IDwcaWriter writer = config.getWriter(out);
			writer.writeDwcaForDataSet();
		}
		catch (DataSetConfigurationException | DataSetWriteException e) {
			throw new DaoException(e);
		}
	}

	@Override
	public String[] dwcaGetDataSetNames()
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("dwcaGetDataSetNames"));
		}
		File dir = DwcaUtil.getDwcaConfigurationDirectory(DwcaDataSetType.TAXON);
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if (f.getName().startsWith("dynamic")) {
					return false;
				}
				if (f.isFile() && f.getName().endsWith(DwcaConfig.CONF_FILE_EXTENSION)) {
					return true;
				}
				return false;
			}
		});
		String[] names = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			names[i] = name.substring(0, name.indexOf('.'));
		}
		Arrays.sort(names);
		return names;
	}

	@Override
	public QueryResult<ScientificNameGroup> groupByScientificName(
			GroupByScientificNameQuerySpec sngQuery) throws InvalidQueryException
	{
		if (logger.isDebugEnabled()) {
			logger.debug(printCall("groupByScientificName", sngQuery));
		}
		return GroupTaxaByScientificNameHelper.groupByScientificName(sngQuery);
	}

	@Override
	Taxon[] createDocumentObjectArray(int length)
	{
		return new Taxon[length];
	}

}
