package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_SPECIMEN;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESSpecimen;
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

public class BrahmsSpecimensImporter {

	public static void main(String[] args) throws Exception
	{
		BrahmsSpecimensImporter importer = new BrahmsSpecimensImporter();
		importer.importCsvFiles();
	}

	static Logger logger = Registry.getInstance().getLogger(BrahmsSpecimensImporter.class);

	private final boolean suppressErrors;

	public BrahmsSpecimensImporter()
	{
		suppressErrors = ConfigObject.isEnabled("brahms.suppress-errors");
	}

	public void importCsvFiles() throws Exception
	{

//		long start = System.currentTimeMillis();
//
//		File[] csvFiles = getCsvFiles();
//		if (csvFiles.length == 0) {
//			logger.info("No new CSV files to import");
//			return;
//		}
//
//		ThemeCache.getInstance().resetMatchCounters();
//
//		CSVExtractor extractor = null;
//		ETLStatistics stats = new ETLStatistics();
//		BrahmsSpecimenTransformer transformer = null;
//		BrahmsSpecimenLoader loader = null;
//
//		try {
//			LoadUtil.truncate(LUCENE_TYPE_SPECIMEN, SourceSystem.BRAHMS);
//			transformer = new BrahmsSpecimenTransformer(stats);
//			loader = new BrahmsSpecimenLoader(stats);
//			for (File f : csvFiles) {
//				logger.info("Processing file " + f.getAbsolutePath());
//				extractor = new CSVExtractor(f);
//				extractor.setSkipHeader(true);
//				extractor.setDelimiter(',');
//				extractor.setCharset(Charset.forName("Windows-1252"));
//				Iterator<CSVRecordInfo> iterator = extractor.iterator();
//				while (iterator.hasNext()) {
//					try {
//						CSVRecordInfo record = iterator.next();
//						List<ESSpecimen> specimens = transformer.transform(record);
//						loader.load(specimens);
//						if (record.getLineNumber() % 50000 == 0) {
//							logger.info("Records processed: " + record.getLineNumber());
//						}
//					}
//					catch (ExtractionException e) {
//						if (!suppressErrors) {
//							logger.error("Line " + e.getLineNumber() + ": " + e.getMessage());
//							logger.error(e.getLine());
//						}
//					}
//				}
//			}
//		}
//		catch (Throwable t) {
//			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
//		}
//		finally {
//			IOUtil.close(loader);
//		}
//
//		ThemeCache.getInstance().logMatchInfo();
//		stats.logStatistics(logger);
//		logger.info(getClass().getSimpleName() + " took " + LoadUtil.getDuration(start));
	}

}