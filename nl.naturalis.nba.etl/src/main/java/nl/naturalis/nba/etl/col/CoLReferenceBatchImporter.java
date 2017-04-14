package nl.naturalis.nba.etl.col;

import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.col.CoLReferenceCsvField.taxonID;

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
public class CoLReferenceBatchImporter {

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
			CoLReferenceBatchImporter importer = new CoLReferenceBatchImporter();
			importer.setBatchSize(batchSize);
			String dwcaDir = DaoRegistry.getInstance().getConfiguration()
					.required("col.data.dir");
			importer.importCsv(dwcaDir + "/reference.txt");
		}
		finally {
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(CoLReferenceBatchImporter.class);

	public CoLReferenceBatchImporter()
	{
	}

	private int batchSize;

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
		CSVExtractor<CoLReferenceCsvField> extractor = createExtractor(stats, f);
		CoLReferenceBatchTransformer transformer = new CoLReferenceBatchTransformer();
		ArrayList<CSVRecordInfo<CoLReferenceCsvField>> csvRecords;
		csvRecords = new ArrayList<>(batchSize);
		ArrayList<Taxon> queue = new ArrayList<>(batchSize);
		int processed = 0;
		logger.info("Processing file {}", f.getAbsolutePath());
		for (CSVRecordInfo<CoLReferenceCsvField> rec : extractor) {
			if (rec == null || rec.get(taxonID) == null) {
				// garbage
				continue;
			}
			csvRecords.add(rec);
			if (csvRecords.size() == batchSize) {
				Collection<Taxon> updates = transformer.transform(csvRecords);
				if (queue.size() + updates.size() >= batchSize) {
					flushQueue(queue);
				}
				queue.addAll(updates);
				csvRecords.clear();
			}
			if (++processed % 100000 == 0) {
				logger.info("Records processed: {}", processed);
				logger.info("References created: {}", transformer.getNumCreated());
			}
		}
		if (csvRecords.size() != 0) {
			Collection<Taxon> updates = transformer.transform(csvRecords);
			if (queue.size() + updates.size() >= batchSize) {
				flushQueue(queue);
			}
			queue.addAll(updates);
		}
		if (queue.size() != 0) {
			flushQueue(queue);
		}
		logger.info("Records processed: {}", processed);
		logger.info("References created: {}", transformer.getNumCreated());
		logger.info("Taxa enriched: {}", transformer.getNumUpdated());
		logger.info("Duplicates: {}", transformer.getNumDuplicates());
		logger.info("Orphans: {}", transformer.getNumOrphans());
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

	private static void flushQueue(ArrayList<Taxon> queue) throws BulkIndexException
	{
		BulkIndexer<Taxon> indexer = new BulkIndexer<>(TAXON);
		indexer.index(queue);
		ESUtil.refreshIndex(TAXON);
		queue.clear();
	}

	private static CSVExtractor<CoLReferenceCsvField> createExtractor(ETLStatistics stats,
			File f)
	{
		CSVExtractor<CoLReferenceCsvField> extractor;
		extractor = new CSVExtractor<>(f, stats);
		extractor.setSkipHeader(true);
		extractor.setDelimiter('\t');
		return extractor;
	}

}
