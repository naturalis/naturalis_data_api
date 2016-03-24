package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.etl.Registry;
import nl.naturalis.nba.etl.ThemeCache;
import nl.naturalis.nba.etl.normalize.SpecimenTypeStatusNormalizer;

import org.apache.logging.log4j.Logger;
import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;

/**
 * Driver class for the import of Brahms multimedia.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{
		BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter();
		importer.importCsvFiles();
	}

	static final Logger logger = Registry.getInstance().getLogger(BrahmsMultiMediaImporter.class);

	private final boolean suppressErrors;

	public BrahmsMultiMediaImporter()
	{
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
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
		try {
			LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, SourceSystem.BRAHMS);
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics globalStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics myStats = new ETLStatistics();
		myStats.setOneToMany(true);
		BrahmsMultiMediaLoader multimediaLoader = null;
		try {
			BrahmsMultiMediaTransformer multimediaTransformer = new BrahmsMultiMediaTransformer(myStats);
			multimediaLoader = new BrahmsMultiMediaLoader(myStats);
			CSVExtractor<BrahmsCsvField> extractor = createExtractor(f, myStats);
			for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
				if (rec == null)
					continue;
				multimediaLoader.load(multimediaTransformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
			}
		}
		finally {
			// Important! Flushes the remaining objects in the ES bulk request
			// batch.
			IOUtil.close(multimediaLoader);
		}
		globalStats.add(myStats);
		myStats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
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
