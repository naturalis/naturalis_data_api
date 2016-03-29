package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.api.model.SourceSystem.BRAHMS;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

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
 * Manages the import of Brahms specimens.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsSpecimenImporter {

	public static void main(String[] args) throws Exception
	{
		BrahmsSpecimenImporter importer = new BrahmsSpecimenImporter();
		importer.importCsvFiles();
	}

	static Logger logger = Registry.getInstance().getLogger(BrahmsSpecimenImporter.class);

	private final boolean suppressErrors;

	public BrahmsSpecimenImporter()
	{
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	/**
	 * Iterates over the CSV files in the brahms data directory and imports
	 * them.
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
		try {
			LoadUtil.truncate(LUCENE_TYPE_SPECIMEN, BRAHMS);
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger, "Specimens");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics globalStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics myStats = new ETLStatistics();
		CSVExtractor<BrahmsCsvField> extractor = null;
		BrahmsSpecimenTransformer transformer = null;
		BrahmsSpecimenLoader loader = null;
		try {
			extractor = createExtractor(f, myStats);
			transformer = new BrahmsSpecimenTransformer(myStats);
			loader = new BrahmsSpecimenLoader(myStats);
			for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
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
		myStats.logStatistics(logger, "Specimens");
		globalStats.add(myStats);
		LoadUtil.logDuration(logger, getClass(), start);
		logger.info(" ");
		logger.info(" ");
	}

	private CSVExtractor<BrahmsCsvField> createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor<BrahmsCsvField> extractor = new CSVExtractor<>(f, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}