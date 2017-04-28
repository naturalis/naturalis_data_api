package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.api.model.SourceSystem.NDFF;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ndff.NdffImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.ETLConstants;
import nl.naturalis.nba.etl.ETLUtil;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

public class NdffSpecimenImporter {

	public static void main(String[] args)
	{
		try {
			NdffSpecimenImporter importer = new NdffSpecimenImporter();
			importer.importSpecimens();
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger;

	static {
		logger = ETLRegistry.getInstance().getLogger(NdffSpecimenImporter.class);
	}

	private final boolean suppressErrors;
	private final int esBulkRequestSize;

	public NdffSpecimenImporter()
	{
		suppressErrors = ConfigObject.isEnabled("ndff.suppress-errors");
		String key = ETLConstants.SYSPROP_LOADER_QUEUE_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	/**
	 * Imports specimen data from the NDFF CSV file(s).
	 */
	public void importSpecimens()
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		ETLStatistics stats = new ETLStatistics();
		try {
			ESUtil.truncate(SPECIMEN, NDFF);
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		stats.logStatistics(logger, "Specimens");
		ETLUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics globalStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics fileStats = new ETLStatistics();
		CSVExtractor<NdffCsvField> extractor = null;
		NdffSpecimenTransformer transformer = null;
		NdffSpecimenLoader loader = null;
		try {
			extractor = createExtractor(f, fileStats);
			transformer = new NdffSpecimenTransformer(fileStats);
			loader = new NdffSpecimenLoader(fileStats, esBulkRequestSize);
			for (CSVRecordInfo<NdffCsvField> rec : extractor) {
				if (rec == null)
					continue;
				loader.queue(transformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		fileStats.logStatistics(logger, "Specimens");
		globalStats.add(fileStats);
		ETLUtil.logDuration(logger, getClass(), start);
		logger.info(" ");
		logger.info(" ");
	}

	private CSVExtractor<NdffCsvField> createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor<NdffCsvField> extractor = new CSVExtractor<>(f, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(';');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
