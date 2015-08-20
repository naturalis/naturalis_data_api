package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ETLStatistics;
import nl.naturalis.nda.elasticsearch.load.ExtractionException;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class BrahmsImportAll {

	public static void main(String[] args)
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			BrahmsImportAll importer = new BrahmsImportAll(index);
			importer.importPerFile();
		}
		catch (Throwable t) {
			logger.error("Brahms import failed!");
			logger.error(t.getMessage(), t);
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	/**
	 * The prefix for ElasticSearch IDs for Brahms documents.
	 */
	public static final String ID_PREFIX = "BRAHMS-";

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsImportAll.class);

	private final IndexNative index;
	private final boolean suppressErrors;
	private final boolean backup;

	public BrahmsImportAll(IndexNative index)
	{
		this.index = index;
		suppressErrors = ConfigObject.TRUE("brahms.suppress-errors");
		backup = ConfigObject.TRUE("brahms.backup", true);
	}

	/**
	 * This method first imports all specimens, then all multimedia.
	 * 
	 * @throws Exception
	 */
	public void importPerType() throws Exception
	{
		BrahmsSpecimensImporter specimenImporter = new BrahmsSpecimensImporter(index);
		specimenImporter.importCsvFiles();
		BrahmsMultiMediaImporter multiMediaImporter = new BrahmsMultiMediaImporter(index);
		multiMediaImporter.importCsvFiles();
		if (backup) {
			String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
			for (File f : getCsvFiles()) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}
	}

	/**
	 * This method imports specimen and multimedia at the same time.
	 * 
	 * @throws Exception
	 */
	public void importPerFile() throws Exception
	{

		long start = System.currentTimeMillis();

		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No CSV files to process");
			return;
		}

		ThemeCache.getInstance().resetMatchCounters();

		CSVExtractor extractor = null;
		BrahmsSpecimenTransformer specimenTransformer = null;
		BrahmsSpecimenLoader specimenLoader = null;
		BrahmsMultiMediaTransformer multimediaTransformer = null;
		BrahmsMultiMediaLoader multimediaLoader = null;

		try {

			index.deleteWhere(LUCENE_TYPE_SPECIMEN, "sourceSystem.code", SourceSystem.BRAHMS.getCode());
			index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());

			ETLStatistics specimenStats = new ETLStatistics();
			ETLStatistics multimediaStats = new ETLStatistics();
			specimenTransformer = new BrahmsSpecimenTransformer();
			specimenLoader = new BrahmsSpecimenLoader(specimenStats);
			multimediaTransformer = new BrahmsMultiMediaTransformer();
			multimediaLoader = new BrahmsMultiMediaLoader(multimediaStats);

			for (File f : csvFiles) {
				logger.info("Processing file " + f.getAbsolutePath());
				extractor = new CSVExtractor(f);
				extractor.setSkipHeader(true);
				extractor.setDelimiter(',');
				extractor.setCharset(Charset.forName("Windows-1252"));
				Iterator<CSVRecordInfo> iterator = extractor.iterator();
				while (iterator.hasNext()) {
					try {
						CSVRecordInfo record = iterator.next();
						specimenLoader.load(specimenTransformer.transform(record));
						multimediaLoader.load(multimediaTransformer.transform(record));
						if (record.getLineNumber() % 50000 == 0) {
							logger.info("Records processed: " + record.getLineNumber());
						}
					}
					catch (ExtractionException e) {
						if (!suppressErrors) {
							logger.error("Line " + e.getLineNumber() + ": " + e.getMessage());
							logger.error(e.getLine());
						}
					}
				}
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		finally {
			IOUtil.close(specimenLoader);
			IOUtil.close(multimediaLoader);
		}

		if (backup) {
			String backupExtension = "." + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".imported";
			for (File f : getCsvFiles()) {
				f.renameTo(new File(f.getAbsolutePath() + backupExtension));
			}
		}

		ThemeCache.getInstance().logMatchInfo();
		logger.info(getClass().getSimpleName() + " took " + LoadUtil.getDuration(start));
	}
}
