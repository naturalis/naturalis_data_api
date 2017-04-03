package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.IOUtil;

/**
 * Enriches CoL taxon documents with synonyms sourced from the taxa&#46;txt file
 * in a CoL DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLSynonymImporter extends CoLImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			CoLSynonymImporter importer = new CoLSynonymImporter();
			String dwcaDir = DaoRegistry.getInstance().getConfiguration().required("col.data.dir");
			importer.importCsv(dwcaDir + "/taxa.txt");
		}
		finally {
			ESUtil.refreshIndex(TAXON);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(CoLSynonymImporter.class);

	public CoLSynonymImporter()
	{
		super();
	}

	/**
	 * Extracts and imports synonyms from the taxa&#46;txt file.
	 * 
	 * @param path
	 */
	public void importCsv(String path)
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = null;
		CSVExtractor<CoLTaxonCsvField> extractor = null;
		CoLSynonymTransformer transformer = null;
		CoLTaxonLoader loader = null;
		try {
			File f = new File(path);
			if (!f.exists())
				throw new ETLRuntimeException("No such file: " + path);
			stats = new ETLStatistics();
			stats.setNested(true);
			extractor = createExtractor(stats, f);
			loader = new CoLTaxonLoader(stats, loaderQueueSize);
			loader.enableQueueLookups(true);
			loader.suppressErrors(suppressErrors);
			transformer = new CoLSynonymTransformer(stats,loader);
			transformer.setSuppressErrors(suppressErrors);
			logger.info("Processing file {}", f.getAbsolutePath());
			for (CSVRecordInfo<CoLTaxonCsvField> rec : extractor) {
				if (rec == null)
					continue;
				List<Taxon> taxa = transformer.transform(rec);
				loader.queue(taxa);
				if (stats.recordsProcessed != 0 && stats.recordsProcessed % 50000 == 0) {
					logger.info("Records processed: {}", stats.recordsProcessed);
					logger.info("Documents indexed: {}", stats.documentsIndexed);
				}
			}
		}
		finally {
			IOUtil.close(loader);
		}
		logger.info("Number of orphans: {}", transformer.getNumOrphans());
		stats.logStatistics(logger);
		logger.info("(NB skipped records are accepted names)");
		logDuration(logger, getClass(), start);

	}

	private CSVExtractor<CoLTaxonCsvField> createExtractor(ETLStatistics stats, File f)
	{
		CSVExtractor<CoLTaxonCsvField> extractor;
		extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setSuppressErrors(suppressErrors);
		return extractor;
	}
}
