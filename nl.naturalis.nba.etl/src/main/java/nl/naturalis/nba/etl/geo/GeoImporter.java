package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.api.model.SourceSystem.GEO;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.etl.geo.GeoImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.etl.ETLConstants;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Imports geo areas into the {@link GeoArea} index.
 * 
 * @author Ayco Holleman
 *
 */
public class GeoImporter {

	public static void main(String[] args)
	{
		try {
			GeoImporter importer = new GeoImporter();
			importer.importAll();
		}
		catch (Throwable t) {
			logger.error("GeoImporter terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(GEO_AREA);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(GeoImporter.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public GeoImporter()
	{
		suppressErrors = ConfigObject.isEnabled("suppressErrors");
		String key = ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
		/*
		 * Queue size not too big b/c documents can become huge because of the
		 * geo json.
		 */
		String val = System.getProperty(key, "10");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Imports specimen data from the Geo Area CSV file(s).
	 */
	public void importAll()
	{
		long start = System.currentTimeMillis();
		ESUtil.truncate(GEO_AREA, GEO);
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		ETLStatistics stats = new ETLStatistics();
		for (File f : csvFiles) {
			processFile(f, stats);
		}
		stats.logStatistics(logger, "Geo Areas");
		ETLUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics globalStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics fileStats = new ETLStatistics();
		CSVExtractor<GeoCsvField> extractor = null;
		GeoTransformer transformer = null;
		GeoLoader loader = null;
		try {
			extractor = createExtractor(f, fileStats);
			transformer = new GeoTransformer(fileStats);
			loader = new GeoLoader(fileStats, esBulkRequestSize);
			for (CSVRecordInfo<GeoCsvField> rec : extractor) {
				if (rec == null)
					continue;
				loader.queue(transformer.transform(rec));
				if (fileStats.recordsProcessed != 0 && fileStats.recordsProcessed % 100 == 0) {
					logger.info("Records processed: {}", fileStats.recordsProcessed);
					if (fileStats.documentsIndexed != 0) {
						logger.info("Documents indexed: {}", fileStats.documentsIndexed);
					}
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		fileStats.logStatistics(logger, "Geo Areas");
		globalStats.add(fileStats);
		ETLUtil.logDuration(logger, getClass(), start);
		logger.info(" ");
		logger.info(" ");
	}

	private CSVExtractor<GeoCsvField> createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor<GeoCsvField> extractor = new CSVExtractor<>(f, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("UTF-8"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
