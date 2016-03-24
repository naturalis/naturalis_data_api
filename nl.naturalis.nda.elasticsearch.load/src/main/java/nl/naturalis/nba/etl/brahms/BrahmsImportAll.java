package nl.naturalis.nba.etl.brahms;

import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nba.etl.NBAImportAll.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.backup;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.getCsvFiles;
import static nl.naturalis.nba.etl.brahms.BrahmsImportUtil.removeBackupExtension;

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
 * Manages the import of Brahms specimens and multimedia. Since specimens and
 * multimedia are extracted from the same CSV record, this class allows you to
 * import either per file or per type (first all specimens, then all
 * multimedia). With the first option each CSV file is processed only once. With
 * the second option each CSV file is processed twice: once for the specimen
 * import and once for the multimedia import. Thus the first option should be
 * faster and is the default. To force a per-type import add
 * {@code -Dbrahms.parallel=false} to the java command line.
 * 
 * @author Ayco Holleman
 *
 */
public class BrahmsImportAll {

	public static void main(String[] args)
	{
		if (args.length == 0)
			new BrahmsImportAll().importPerFile();
		else if (args[0].equalsIgnoreCase("backup"))
			new BrahmsImportAll().backupSourceFiles();
		else if (args[0].equalsIgnoreCase("reset"))
			new BrahmsImportAll().reset();
		else
			logger.error("Invalid argument: " + args[0]);
	}

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportAll.class);

	private final boolean backup;
	private final boolean parallel;
	private final boolean suppressErrors;

	public BrahmsImportAll()
	{
		backup = ConfigObject.isEnabled("brahms.backup", true);
		parallel = ConfigObject.isEnabled("brahms.parallel", true);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	/**
	 * Import specimens and multimedia either in parallel fashion or in serial
	 * fashion, depending on the {@code brahms.parallel} system property.
	 */
	public void importAll()
	{
		if (parallel)
			importPerFile();
		else
			importPerType();
	}

	/**
	 * This method first imports all specimens, then all multimedia. Thus each
	 * CSV file is read twice.
	 * 
	 */
	public void importPerType()
	{
		BrahmsSpecimenImporter specimenImporter = new BrahmsSpecimenImporter();
		specimenImporter.importCsvFiles();
		BrahmsMultiMediaImporter multiMediaImporter = new BrahmsMultiMediaImporter();
		multiMediaImporter.importCsvFiles();
		if (backup) {
			backup();
		}
	}

	/**
	 * This method processes each CSV files only once, extracting and loading
	 * both specimens and multimedia at the same time.
	 * 
	 */
	public void importPerFile()
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		SpecimenTypeStatusNormalizer.getInstance().resetStatistics();
		ThemeCache.getInstance().resetMatchCounters();
		// Global statistics for specimen import
		ETLStatistics sStats = new ETLStatistics();
		// Global statistics for multimedia import
		ETLStatistics mStats = new ETLStatistics();
		mStats.setOneToMany(true);
		try {
			LoadUtil.truncate(LUCENE_TYPE_SPECIMEN, SourceSystem.BRAHMS);
			LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, SourceSystem.BRAHMS);
			for (File f : csvFiles) {
				processFile(f, sStats, mStats);
			}
			if (backup) {
				backup();
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		SpecimenTypeStatusNormalizer.getInstance().logStatistics();
		ThemeCache.getInstance().logMatchInfo();
		sStats.logStatistics(logger, "Specimens");
		mStats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	/**
	 * Backs up the CSV files in the Brahms data directory by appending a
	 * "&#46;imported" extension to the file name.
	 */
	public void backupSourceFiles()
	{
		backup();
	}

	/**
	 * Removes the "&#46;imported" file name extension from the files in the
	 * Brahms data directory. Nice for repitive testing. Not meant for
	 * production purposes.
	 */
	public void reset()
	{
		removeBackupExtension();
	}

	private void processFile(File f, ETLStatistics sStats, ETLStatistics mStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		ETLStatistics specimenStats = new ETLStatistics();
		ETLStatistics multimediaStats = new ETLStatistics();
		multimediaStats.setOneToMany(true);
		ETLStatistics extractionStats = new ETLStatistics();
		CSVExtractor<BrahmsCsvField> extractor = null;
		BrahmsSpecimenTransformer specimenTransformer = null;
		BrahmsMultiMediaTransformer multimediaTransformer = null;
		BrahmsSpecimenLoader specimenLoader = null;
		BrahmsMultiMediaLoader multimediaLoader = null;
		try {
			extractor = createExtractor(f, extractionStats);
			specimenTransformer = new BrahmsSpecimenTransformer(specimenStats);
			specimenLoader = new BrahmsSpecimenLoader(specimenStats);
			multimediaTransformer = new BrahmsMultiMediaTransformer(multimediaStats);
			multimediaLoader = new BrahmsMultiMediaLoader(multimediaStats);
			for (CSVRecordInfo<BrahmsCsvField> rec : extractor) {
				if (rec == null)
					continue;
				specimenLoader.load(specimenTransformer.transform(rec));
				multimediaLoader.load(multimediaTransformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0)
					logger.info("Records processed: " + rec.getLineNumber());
			}
		}
		finally {
			IOUtil.close(specimenLoader, multimediaLoader);
		}
		specimenStats.add(extractionStats);
		multimediaStats.add(extractionStats);
		specimenStats.logStatistics(logger, "Specimens");
		multimediaStats.logStatistics(logger, "Multimedia");
		sStats.add(specimenStats);
		mStats.add(multimediaStats);
		logger.info("Importing " + f.getName() + " took " + LoadUtil.getDuration(start));
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
