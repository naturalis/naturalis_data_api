package nl.naturalis.nba.etl.geo;

import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.etl.geo.GeoImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;

public class GeoImporter {

	public static void main(String[] args)
	{
		boolean bootstrap = Arrays.asList(args).contains("bootstrap");
		try {
			GeoImporter importer = new GeoImporter();
			importer.importData(bootstrap);
		}
		finally {
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
		suppressErrors = ConfigObject.isEnabled("geo.suppress-errors");
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "10");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Imports specimen data from the Geo Area CSV file(s).
	 */
	public void importData(boolean bootstrap)
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		ETLStatistics stats = new ETLStatistics();
		try {
			if(bootstrap) {
				ESUtil.deleteIndex(GEO_AREA);
				ESUtil.createIndex(GEO_AREA);
				ESUtil.createType(GEO_AREA);
			}
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		stats.logStatistics(logger, "Geo Areas");
		LoadUtil.logDuration(logger, getClass(), start);
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
				loader.load(transformer.transform(rec));
				if (rec.getLineNumber() % 100 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		fileStats.logStatistics(logger, "Geo Areas");
		globalStats.add(fileStats);
		LoadUtil.logDuration(logger, getClass(), start);
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
