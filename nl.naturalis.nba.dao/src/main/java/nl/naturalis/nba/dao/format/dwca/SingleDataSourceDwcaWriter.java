package nl.naturalis.nba.dao.format.dwca;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DaoUtil;
import nl.naturalis.nba.dao.format.DataSetConfigurationException;
import nl.naturalis.nba.dao.format.DataSetWriteException;
import nl.naturalis.nba.dao.format.Entity;
import nl.naturalis.nba.dao.util.RandomEntryZipOutputStream;
import nl.naturalis.nba.dao.util.es.IScroller;
import nl.naturalis.nba.utils.IOUtil;

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

	/**
	 * Writes a DwCA archive for a user-defined query (a&#46;k&#46;a&#46;
	 * "dynamic DwCA").
	 */
	@Override
	public void writeDwcaForQuery(QuerySpec query)
			throws InvalidQueryException, DataSetConfigurationException, DataSetWriteException
	{
		long start = System.currentTimeMillis();
		logger.info("Generating DarwinCore archive for user-defined query");
		DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
		dwcaPreparator.prepare();
		IScroller scroller = cfg.createScroller(query);
		RandomEntryZipOutputStream rezos = createRandomEntryZipOutputStream();
		SingleDataSourceSearchHitHandler handler = new SingleDataSourceSearchHitHandler(cfg, rezos);
		/*
		 * OK, we are going to send bytes over the line, we're past the point
		 * that we can respond with an error message if anything goes wrong
		 */
		logger.info("Writing CSV file(s)");
		try {
			handler.printHeaders();
			scroller.scroll(handler);
			handler.logStatistics();
			rezos.flush();
		}
		catch (Throwable t) {
			String msg = "Error writing archive for user-defined query";
			logger.error(msg, t);
			try {
				ZipOutputStream zos = rezos.mergeEntries();
				zos.putNextEntry(new ZipEntry("__ERROR__.txt"));
				t.printStackTrace(new PrintStream(zos));
				zos.finish();
			}
			catch (Throwable t2) {
				logger.error(t2);
			}
			return;
		}
		try {
			ZipOutputStream zos = rezos.mergeEntries();
			logger.info("Writing meta.xml");
			zos.putNextEntry(new ZipEntry("meta.xml"));
			zos.write(dwcaPreparator.getMetaXml());
			logger.info("Writing eml.xml ({})", cfg.getEmlFile());
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getEml());
			zos.finish();
			String took = DaoUtil.getDuration(start);
			logger.info("DarwinCore archive generated (took {})", took);
		}
		catch (Throwable t) {
			String msg = "Error writing archive for user-defind query";
			logger.error(msg, t);
		}
	}

	/**
	 * Writes a DwCA archive for a predefined dataset (the query to be executed
	 * is in the XML configuration file for the dataset).
	 */
	@Override
	public void writeDwcaForDataSet() throws DataSetConfigurationException, DataSetWriteException
	{
		long start = System.currentTimeMillis();
		String fmt = "Generating DarwinCore archive for data set \"{}\"";
		logger.info(fmt, cfg.getDataSetName());
		DwcaPreparator dwcaPreparator = new DwcaPreparator(cfg);
		dwcaPreparator.prepare();
		QuerySpec query = cfg.getDataSet().getSharedDataSource().getQuerySpec();
		IScroller scroller;
		try {
			scroller = cfg.createScroller(query);
		}
		catch (InvalidQueryException e) {
			// Not user's fault, query comes from the configuration file
			throw new DataSetConfigurationException(e);
		}
		RandomEntryZipOutputStream rezos = createRandomEntryZipOutputStream();
		SingleDataSourceSearchHitHandler handler = new SingleDataSourceSearchHitHandler(cfg, rezos);
		/*
		 * OK, we are going to send bytes over the line, we're past the point
		 * that we can respond with an error message if anything goes wrong
		 */
		logger.info("Writing CSV file(s)");
		try {
			handler.printHeaders();
			scroller.scroll(handler);
			handler.logStatistics();
			rezos.flush();
		}
		catch (Throwable t) {
			String msg = "Error while writing archive for dataset " + cfg.getDataSetName();
			logger.error(msg, t);
			try {
				ZipOutputStream zos = rezos.mergeEntries();
				zos.putNextEntry(new ZipEntry("__ERROR__.txt"));
				t.printStackTrace(new PrintStream(zos));
				zos.finish();
			}
			catch (Throwable t2) {
				logger.error(t2);
			}
			return;
		}
		try {
			ZipOutputStream zos = rezos.mergeEntries();
			logger.info("Writing meta.xml");
			zos.putNextEntry(new ZipEntry("meta.xml"));
			zos.write(dwcaPreparator.getMetaXml());
			logger.info("Writing eml.xml ({})", cfg.getEmlFile());
			zos.putNextEntry(new ZipEntry("eml.xml"));
			zos.write(dwcaPreparator.getEml());
			zos.finish();
			String took = DaoUtil.getDuration(start);
			logger.info("DarwinCore archive generated (took {})", took);
		}
		catch (Throwable t) {
			String msg = "Error writing archive for dataset " + cfg.getDataSetName();
			logger.error(msg, t);
		}
	}

	private RandomEntryZipOutputStream createRandomEntryZipOutputStream()
			throws DataSetConfigurationException, DataSetWriteException
	{
		Entity coreEntity = cfg.getCoreEntity();
		String fileName = cfg.getCsvFileName(coreEntity);
		RandomEntryZipOutputStream rezos = null;
		try {
			rezos = new RandomEntryZipOutputStream(out, fileName);
		}
		catch (IOException exc) {
			IOUtil.close(rezos);
			throw new DataSetWriteException(exc);
		}
		HashSet<String> fileNames = new HashSet<>();
		for (Entity e : cfg.getDataSet().getEntities()) {
			/*
			 * NB Multiple entities may get written to the same zip entry (e.g.
			 * taxa and synonyms are both written to taxa.txt). Thus we must
			 * make sure to create only unique zip entries.
			 */
			fileName = cfg.getCsvFileName(e);
			if (fileNames.contains(fileName)) {
				continue;
			}
			fileNames.add(fileName);
			if (e.getName().equals(coreEntity.getName())) {
				continue;
			}
			try {
				rezos.addEntry(fileName, 1024 * 1024);
			}
			catch (IOException exc) {
				IOUtil.close(rezos);
				throw new DataSetWriteException(exc);
			}
		}
		return rezos;
	}

}
