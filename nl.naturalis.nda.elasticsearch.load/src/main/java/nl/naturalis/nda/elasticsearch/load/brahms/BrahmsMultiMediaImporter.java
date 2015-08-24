package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
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

	public void importCsvFiles()
	{
		long start = System.currentTimeMillis();
		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}
		ThemeCache.getInstance().resetMatchCounters();
		ETLStatistics stats = new ETLStatistics();
		try {
			LoadUtil.truncate(LUCENE_TYPE_MULTIMEDIA_OBJECT, SourceSystem.BRAHMS);
			for (File f : csvFiles) {
				processFile(f, stats);
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		ThemeCache.getInstance().logMatchInfo();
		stats.logStatistics(logger, "Multimedia");
		LoadUtil.logDuration(logger, getClass(), start);
	}

	private void processFile(File f, ETLStatistics mStats)
	{
		long start = System.currentTimeMillis();
		logger.info("Processing file " + f.getAbsolutePath());
		BrahmsMultiMediaLoader multimediaLoader = null;
		try {
			ETLStatistics multimediaStats = new ETLStatistics();
			ETLStatistics extractionStats = new ETLStatistics();
			BrahmsMultiMediaTransformer multimediaTransformer = new BrahmsMultiMediaTransformer(multimediaStats);
			multimediaLoader = new BrahmsMultiMediaLoader(multimediaStats);
			CSVExtractor extractor = createExtractor(f, extractionStats);
			for (CSVRecordInfo rec : extractor) {
				if (rec == null)
					continue;
				multimediaLoader.load(multimediaTransformer.transform(rec));
				if (rec.getLineNumber() % 50000 == 0) {
					logger.info("Records processed: " + rec.getLineNumber());
				}
			}
			multimediaStats.add(extractionStats);
			multimediaStats.logStatistics(logger, "Multimedia");
			mStats.add(multimediaStats);
			logger.info("Importing " + f.getName() + " took " + LoadUtil.getDuration(start));
			logger.info(" ");
			logger.info(" ");
		}
		finally {
			IOUtil.close(multimediaLoader);
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
