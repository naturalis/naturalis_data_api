package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeEmlXml;
import static nl.naturalis.nba.dao.format.dwca.DwcaUtil.writeMetaXml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DaoUtil;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.IScroller;

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
		long start = System.currentTimeMillis();
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
		String took = DaoUtil.getDuration(start);
		logger.info("DarwinCore archive generated (took {})", took);
	}

	@Override
	public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException
	{
		long start = System.currentTimeMillis();
		String fmt = "Generating DarwinCore archive for data set \"{}\"";
		logger.info(fmt, cfg.getDataSetName());
		QuerySpec query = cfg.getDataSet().getSharedDataSource().getQuerySpec();
		try {
			RandomEntryZipOutputStream zip = createZipStream();
			logger.info("Adding CSV files");
			writeCsvFiles(query, zip);
			ZipOutputStream zos = zip.mergeEntries();
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
		String took = DaoUtil.getDuration(start);
		logger.info("DarwinCore archive generated (took {})", took);
	}

	private void writeCsvFiles(QuerySpec query, RandomEntryZipOutputStream zip)
			throws DataSetConfigurationException, DataSetWriteException, IOException,
			InvalidQueryException
	{
		IScroller scroller = cfg.createScroller(query);
		SingleDataSourceSearchHitHandler handler = new SingleDataSourceSearchHitHandler(cfg, zip);
		handler.printHeaders();
		try {
			scroller.scroll(handler);
		}
		catch (NbaException e) {
			throw (DataSetWriteException) e;
		}
		handler.logStatistics();
		zip.flush();
	}

	private RandomEntryZipOutputStream createZipStream()
			throws DataSetConfigurationException, IOException
	{
		Entity coreEntity = cfg.getCoreEntity();
		String fileName = cfg.getCsvFileName(coreEntity);
		RandomEntryZipOutputStream rezos;
		rezos = new RandomEntryZipOutputStream(out, fileName);
		HashSet<String> fileNames = new HashSet<>();
		for (Entity e : cfg.getDataSet().getEntities()) {
			/*
			 * NB Multiple entities may get written to the same zip entry (e.g.
			 * taxa and synonyms are both written to taxa.txt. Thus we must make
			 * sure to create only unique zip entries.
			 */
			fileName = cfg.getCsvFileName(e);
			if (fileNames.contains(fileName)) {
				continue;
			}
			fileNames.add(fileName);
			if (e.getName().equals(coreEntity.getName())) {
				continue;
			}
			rezos.addEntry(fileName, 1024 * 1024);
		}
		return rezos;
	}

}
