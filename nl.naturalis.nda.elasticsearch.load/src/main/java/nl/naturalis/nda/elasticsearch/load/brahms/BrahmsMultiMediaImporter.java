package nl.naturalis.nda.elasticsearch.load.brahms;

import static nl.naturalis.nda.elasticsearch.load.NDAIndexManager.LUCENE_TYPE_MULTIMEDIA_OBJECT;
import static nl.naturalis.nda.elasticsearch.load.brahms.BrahmsImportUtil.getCsvFiles;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import nl.naturalis.nda.domain.SourceSystem;
import nl.naturalis.nda.elasticsearch.client.IndexNative;
import nl.naturalis.nda.elasticsearch.dao.estypes.ESMultiMediaObject;
import nl.naturalis.nda.elasticsearch.load.CSVExtractor;
import nl.naturalis.nda.elasticsearch.load.CSVRecordInfo;
import nl.naturalis.nda.elasticsearch.load.ExtractionException;
import nl.naturalis.nda.elasticsearch.load.LoadUtil;
import nl.naturalis.nda.elasticsearch.load.Registry;
import nl.naturalis.nda.elasticsearch.load.ThemeCache;

import org.domainobject.util.ConfigObject;
import org.domainobject.util.IOUtil;
import org.slf4j.Logger;

public class BrahmsMultiMediaImporter {

	public static void main(String[] args) throws Exception
	{
		IndexNative index = null;
		try {
			index = Registry.getInstance().getNbaIndexManager();
			BrahmsMultiMediaImporter importer = new BrahmsMultiMediaImporter(index);
			importer.importCsvFiles();
		}
		finally {
			if (index != null) {
				index.getClient().close();
			}
		}
	}

	private static final Logger logger = Registry.getInstance().getLogger(BrahmsMultiMediaImporter.class);

	private final IndexNative index;
	private final boolean suppressErrors;

	public BrahmsMultiMediaImporter(IndexNative index)
	{
		this.index = index;
		suppressErrors = ConfigObject.TRUE("brahms.suppress-errors");
	}

	public void importCsvFiles()
	{

		long start = System.currentTimeMillis();

		File[] csvFiles = getCsvFiles();
		if (csvFiles.length == 0) {
			logger.info("No new CSV files to import");
			return;
		}

		ThemeCache.getInstance().resetMatchCounters();

		CSVExtractor extractor = null;
		BrahmsMultiMediaTransformer transformer = null;
		BrahmsMultiMediaLoader loader = null;

		index.deleteWhere(LUCENE_TYPE_MULTIMEDIA_OBJECT, "sourceSystem.code", SourceSystem.BRAHMS.getCode());

		try {
			transformer = new BrahmsMultiMediaTransformer();
			loader = new BrahmsMultiMediaLoader(index);
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
						List<ESMultiMediaObject> multimedia = transformer.transform(record);
						loader.load(multimedia);
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
			IOUtil.close(loader);
		}
		
		ThemeCache.getInstance().logMatchInfo();
		logger.info(getClass().getSimpleName() + " took " + LoadUtil.getDuration(start));
	}

}
