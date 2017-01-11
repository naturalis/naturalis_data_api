package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeEmlXml;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeMetaXml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.Scroller;

/**
 * Manages the assembly and creation of DarwinCore archives. Use this class if
 * all CSV files in the archive can be generated from a single query (specified
 * using the &lt;shared-data-source&gt; element in the XML configuration file).
 * 
 * @author Ayco Holleman
 *
 */
class SingleDataSourceDwcaWriter implements IDwcaWriter {

	private static final Logger logger = getLogger(SingleDataSourceDwcaWriter.class);

	private DwcaConfig cfg;
	private OutputStream out;

	SingleDataSourceDwcaWriter(DwcaConfig dwcaConfig, OutputStream out)
	{
		this.cfg = dwcaConfig;
		this.out = out;
	}

	@Override
	public void writeDwcaForQuery(QuerySpec querySpec)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException
	{
		logger.info("Generating DarwinCore archive for user-defined query");
		try {
			RandomEntryZipOutputStream rezos = createZipStream();
			writeCsvFiles(querySpec, rezos);
			ZipOutputStream zos = rezos.mergeEntries();
			writeMetaXml(cfg, zos);
			writeEmlXml(cfg, zos);
			zos.finish();
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		logger.info("Finished writing DarwinCore archive for user-defined query");
	}

	@Override
	public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException
	{
		String fmt = "Generating DarwinCore archive for data set \"{}\"";
		logger.info(fmt, cfg.getDataSetName());
		QuerySpec query = cfg.getDataSet().getSharedDataSource().getQuerySpec();
		try {
			RandomEntryZipOutputStream rezos = createZipStream();
			logger.info("Adding CSV files");
			writeCsvFiles(query, rezos);
			ZipOutputStream zos = rezos.mergeEntries();
			writeMetaXml(cfg, zos);
			writeEmlXml(cfg, zos);
			zos.finish();
		}
		catch (InvalidQueryException e) {
			/*
			 * Not the user's fault but the application maintainer's: the query
			 * was defined in the XML configuration file. So we convert the
			 * InvalidQueryException to a DataSetConfigurationException
			 */
			fmt = "Invalid query specification for shared data source:\n%s";
			String queryString = JsonUtil.toPrettyJson(query);
			String msg = String.format(fmt, queryString);
			throw new DataSetConfigurationException(msg);
		}
		catch (IOException e) {
			throw new DataSetWriteException(e);
		}
		fmt = "Finished writing DarwinCore archive for data set \"{}\"";
		logger.info(fmt, cfg.getDataSetName());
	}

	private void writeCsvFiles(QuerySpec query, RandomEntryZipOutputStream rezos)
			throws DataSetConfigurationException, DataSetWriteException, IOException,
			InvalidQueryException
	{
		SingleDataSourceSearchHitHandler handler = new SingleDataSourceSearchHitHandler(cfg, rezos);
		Scroller scroller;
		if (cfg.getDataSetType() == DwcaDataSetType.TAXON) {
			scroller = new Scroller(query, DocumentType.TAXON, handler);
		}
		else if (cfg.getDataSetType() == DwcaDataSetType.SPECIMEN) {
			scroller = new Scroller(query, DocumentType.SPECIMEN, handler);
		}
		else {
			/*
			 * This really is a program error, so we don't throw a
			 * DataSetWriteException
			 */
			String msg = "Unsupported data set type: " + cfg.getDataSetType();
			throw new DaoException(msg);
		}
		scroller.setTimeout(1000);
		handler.printHeaders();
		try {
			scroller.scroll();
		}
		catch (NbaException e) {
			throw (DataSetWriteException) e;
		}
		handler.logStatistics();
		rezos.flush();
	}

	private RandomEntryZipOutputStream createZipStream()
			throws DataSetConfigurationException, DataSetWriteException, IOException
	{
		Entity coreEntity = cfg.getCoreEntity();
		String fileName = cfg.getCsvFileName(coreEntity);
		RandomEntryZipOutputStream rezos;
		rezos = new RandomEntryZipOutputStream(out, fileName);
		for (Entity e : cfg.getDataSet().getEntities()) {
			if (e.getName().equals(coreEntity.getName())) {
				continue;
			}
			fileName = cfg.getCsvFileName(e);
			rezos.addEntry(fileName, 1024 * 1024);
		}
		return rezos;
	}

}
