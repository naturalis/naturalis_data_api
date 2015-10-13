package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NBAImportAll.LUCENE_TYPE_MULTIMEDIA_OBJECT;
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
			CSVExtractor extractor = createExtractor(f, myStats);
			for (CSVRecordInfo rec : extractor) {
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

	private CSVExtractor createExtractor(File f, ETLStatistics stats)
	{
		CSVExtractor extractor = new CSVExtractor(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter(',');
		extractor.setCharset(Charset.forName("Windows-1252"));
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
