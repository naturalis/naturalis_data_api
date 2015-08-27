package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
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

	public void importCsvFiles() throws Exception
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
		try {
			LoadUtil.truncate(LUCENE_TYPE_SPECIMEN, SourceSystem.BRAHMS);
			for (File f : csvFiles) {
				processFile(f, sStats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		ThemeCache.getInstance().logMatchInfo();
		sStats.logStatistics(logger, "Specimens");
		LoadUtil.logDuration(logger, getClass(), start);
	}
	
	private void processFile(File f, ETLStatistics sStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		BrahmsSpecimenLoader specimenLoader = null;
		try {
			ETLStatistics specimenStats = new ETLStatistics();
			ETLStatistics extractionStats = new ETLStatistics();
			BrahmsSpecimenTransformer specimenTransformer = new BrahmsSpecimenTransformer(specimenStats);
			specimenLoader = new BrahmsSpecimenLoader(specimenStats);
			CSVExtractor extractor = createExtractor(f, extractionStats);
			for (CSVRecordInfo rec : extractor) {
				if (rec == null)
					continue;
				specimenLoader.load(specimenTransformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
			}
			specimenStats.add(extractionStats);
			specimenStats.logStatistics(logger, "Specimens");
			sStats.add(specimenStats);
			logger.info("Importing " + f.getName() + " took " + LoadUtil.getDuration(start));
			logger.info(" ");
			logger.info(" ");
		}
		finally {
			IOUtil.close(specimenLoader);
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