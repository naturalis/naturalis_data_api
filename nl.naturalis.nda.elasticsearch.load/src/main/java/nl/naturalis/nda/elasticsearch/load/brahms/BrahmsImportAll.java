package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.backup;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class BrahmsImportAll {

	public static void main(String[] args)
	{
		BrahmsImportAll importer = new BrahmsImportAll();
		importer.importPerFile();
	}

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportAll.class);

	private final boolean backup;
	private final boolean suppressErrors;

	public BrahmsImportAll()
	{
		backup = ConfigObject.isEnabled("brahms.backup", true);
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	/**
	 * This method first imports all specimens, then all multimedia. Thus each
	 * CSV file is read twice.
	 * 
	 * @throws Exception
	 */
	public void importPerType() throws Exception
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
	 * This method iterates over the CSV files once, importing both specimens
	 * and multimedia at the same time.
	 * 
	 * @throws Exception
	 */
	public void importPerFile()
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		ThemeCache.getInstance().resetMatchCounters();
		// Statistics for specimen import
		ETLStatistics sStats = new ETLStatistics();
		// Statistics for multimedia import
		ETLStatistics mStats = new ETLStatistics();
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
		ThemeCache.getInstance().logMatchInfo();
		sStats.logStatistics(logger, "Specimens");
		mStats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics sStats, ETLStatistics mStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		BrahmsSpecimenLoader specimenLoader = null;
		BrahmsMultiMediaLoader multimediaLoader = null;
		try {
			ETLStatistics specimenStats = new ETLStatistics();
			ETLStatistics multimediaStats = new ETLStatistics();
			ETLStatistics extractionStats = new ETLStatistics();
			BrahmsSpecimenTransformer specimenTransformer = new BrahmsSpecimenTransformer(specimenStats);
			specimenLoader = new BrahmsSpecimenLoader(specimenStats);
			BrahmsMultiMediaTransformer multimediaTransformer = new BrahmsMultiMediaTransformer(multimediaStats);
			multimediaLoader = new BrahmsMultiMediaLoader(multimediaStats);
			CSVExtractor extractor = createExtractor(f, extractionStats);
			for (CSVRecordInfo rec : extractor) {
				if (rec == null)
					continue;
				specimenLoader.load(specimenTransformer.transform(rec));
				multimediaLoader.load(multimediaTransformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
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
		finally {
			IOUtil.close(specimenLoader, multimediaLoader);
		}
	}

	private CSVExtractor createExtractor(File f, ETLStatistics extractionStats)
	{
		CSVExtractor extractor = new CSVExtractor(f, extractionStats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
