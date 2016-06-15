package nl.naturalis.nba.etl.ndff;

import static nl.naturalis.nba.api.model.SourceSystem.NDFF;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nba.etl.ndff.NdffImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.ETLRegistry;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

public class NdffSpecimenImporter {

	public static void main(String[] args)
	{
		try {
			NdffSpecimenImporter importer = new NdffSpecimenImporter();
			importer.importSpecimens();
		}
		finally {
			ETLRegistry.getInstance().closeESClient();
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
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
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
			LoadUtil.truncate(LUCENE_TYPE_SPECIMEN, NDFF);
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		stats.logStatistics(logger, "Specimens");
		LoadUtil.logDuration(logger, getClass(), start);
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
				loader.load(transformer.transform(rec));
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
		LoadUtil.logDuration(logger, getClass(), start);
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
