package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLVernacularNameCsvField.taxonID;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;
import nl.naturalis.nba.etl.CSVExtractor;
import nl.naturalis.nba.etl.CSVRecordInfo;
import nl.naturalis.nba.etl.ETLRuntimeException;
import nl.naturalis.nba.etl.ETLStatistics;

/**
 * Enriches Taxon documents with literature references sourced from the reference.txt file
 * in a CoL DwC archive.
 * 
 * @author Ayco Holleman
 *
 */
public class CoLVernacularNameBatchImporter {

	public static void main(String[] args) throws Exception
	{
		String prop = System.getProperty("batchSize", "1000");
		int batchSize = 0;
		try {
			batchSize = Integer.parseInt(prop);
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid batch size: " + prop);
		}
		if (batchSize >= 1024) {
			// Elasticsearch ids query won't let you look up more than 1024 at once.
			throw new ETLRuntimeException("Batch size exceeds maximum of 1024");
		}
		try {
			CoLVernacularNameBatchImporter importer = new CoLVernacularNameBatchImporter();
			importer.setBatchSize(batchSize);
			String dwcaDir = DaoRegistry.getInstance().getConfiguration()
					.required("col.data.dir");
			importer.importCsv(dwcaDir + "/vernacular.txt");
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(CoLVernacularNameBatchImporter.class);

	public CoLVernacularNameBatchImporter()
	{
	}

	private int batchSize = 1000;

	/**
	 * Processes the reference.txt file
	 * 
	 * @param path
	 * @throws BulkIndexException
	 */
	public void importCsv(String path) throws BulkIndexException
	{
		File f = new File(path);
		if (!f.exists()) {
			throw new ETLRuntimeException("No such file: " + path);
		}
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		CSVExtractor<CoLVernacularNameCsvField> extractor = createExtractor(stats, f);
		CoLVernacularNameBatchTransformer transformer = new CoLVernacularNameBatchTransformer();
		BulkIndexer<Taxon> indexer = new BulkIndexer<>(TAXON);
		ArrayList<CSVRecordInfo<CoLVernacularNameCsvField>> csvRecords;
		csvRecords = new ArrayList<>(batchSize);
		int processed = 0;
		logger.info("Processing file {}", f.getAbsolutePath());
		logger.info("Batch size: {}", batchSize);
		for (CSVRecordInfo<CoLVernacularNameCsvField> rec : extractor) {
			if (rec == null || rec.get(taxonID) == null) {
				// Garbage
				continue;
			}
			csvRecords.add(rec);
			if (csvRecords.size() == batchSize) {
				Collection<Taxon> updates = transformer.transform(csvRecords);
				if (updates.size() != 0) {
					indexer.index(updates);
					ESUtil.refreshIndex(TAXON);
				}
				csvRecords.clear();
			}
			if (++processed % 100000 == 0) {
				logger.info("Records processed: {}", processed);
				logger.info("Vernacular names created: {}", transformer.getNumCreated());
			}
		}
		if (csvRecords.size() != 0) {
			Collection<Taxon> updates = transformer.transform(csvRecords);
			if (updates.size() != 0) {
				indexer.index(updates);
				ESUtil.refreshIndex(TAXON);
			}
		}
		logger.info("Records processed: {}", processed);
		logger.info("Vernacular names created: {}", transformer.getNumCreated());
		logger.info("Taxa enriched: {}", transformer.getNumUpdated());
		logger.info("Duplicates: {}", transformer.getNumDuplicates());
		logger.info("Orphans : {}", transformer.getNumOrphans());
		logDuration(logger, getClass(), start);
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	private static CSVExtractor<CoLVernacularNameCsvField> createExtractor(
			ETLStatistics stats, File f)
	{
		CSVExtractor<CoLVernacularNameCsvField> extractor;
		extractor = new CSVExtractor<>(f, CoLVernacularNameCsvField.class, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		extractor.setQuote('\u0000');
		return extractor;
	}

}
