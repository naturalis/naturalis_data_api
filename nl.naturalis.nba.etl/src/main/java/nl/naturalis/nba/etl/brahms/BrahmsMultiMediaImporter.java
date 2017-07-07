package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.dao.DocumentType.MULTI_MEDIA_OBJECT;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_SUPPRESS_ERRORS;
import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_TRUNCATE;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Driver class for the import of Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter();
			importer.importCsvFiles();
		}
		catch (Throwable t) {
			logger.error("BrahmsMultiMediaImporter terminated unexpectedly!", t);
			System.exit(1);
		}
		finally {
			ESUtil.refreshIndex(MULTI_MEDIA_OBJECT);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(BrahmsMultiMediaImporter.class);

	private final int loaderQueueSize;
	private final boolean suppressErrors;

	public BrahmsMultiMediaImporter()
	{
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String val = System.getProperty(SYSPROP_LOADER_QUEUE_SIZE, "1000");
		loaderQueueSize = Integer.parseInt(val);
	}

	/**
	 * Imports the Source files in the Brahms data directory.
	 */
	public void importCsvFiles()
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		SpecimenTypeStatusNormalizer.getInstance().resetStatistics();
		ThemeCache.getInstance().resetMatchCounters();
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		if (ConfigObject.isEnabled(SYSPROP_TRUNCATE, true)) {
			ESUtil.truncate(MULTI_MEDIA_OBJECT, BRAHMS);
		}
		for (File f : csvFiles) {
			processFile(f, stats);
		}
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger, "Multimedia");
		logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics globalStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file {}", f.getAbsolutePath());
		ETLStatistics myStats = new ETLStatistics();
		myStats.setOneToMany(true);
		CSVExtractor<BrahmsCsvField> extractor = null;
		BrahmsMultiMediaTransformer transformer = null;
		BrahmsMultiMediaLoader loader = null;
		try {
			extractor = createExtractor(f, myStats);
			transformer = new BrahmsMultiMediaTransformer(myStats);
			loader = new BrahmsMultiMediaLoader(loaderQueueSize, myStats);
			for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
				if (rec == null)
					continue;
				loader.queue(transformer.transform(rec));
				if (myStats.recordsProcessed != 0 && myStats.recordsProcessed % 50000 == 0) {
					logger.info("Records processed: {}", myStats.recordsProcessed);
					logger.info("Documents indexed: {}", myStats.documentsIndexed);
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		globalStats.add(myStats);
		myStats.logStatistics(logger, "Multimedia");
		logDuration(logger, getClass(), start);
		logger.info(" ");
		logger.info(" ");
	}

	private CSVExtractor<BrahmsCsvField> createExtractor(File f, ETLStatistics stats)
	{
		CSVExtractor<BrahmsCsvField> extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
