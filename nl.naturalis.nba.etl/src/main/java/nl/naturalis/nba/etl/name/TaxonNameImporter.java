package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.*;
import static nl.naturalis.nba.dao.util.es.ESUtil.disableAutoRefresh;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.setAutoRefreshInterval;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.BulkIndexer;

class TaxonNameImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			ESUtil.deleteIndex(SCIENTIFIC_NAME_GROUP);
			ESUtil.createIndex(SCIENTIFIC_NAME_GROUP);
			TaxonNameImporter importer = new TaxonNameImporter();
			importer.importNames();
		}
		catch (Exception e) {
			logger.fatal("TaxonNameImporter aborted unexpectedly", e);
			throw e;
		}
		finally {
			ESUtil.refreshIndex(SCIENTIFIC_NAME_GROUP);
			ESClientManager.getInstance().closeClient();
		}
	}

	private static final Logger logger = getLogger(TaxonNameImporter.class);

	private boolean suppressErrors;
	private int batchSize = 1000;
	private int timeout = 60000;

	void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Taxon> extractor;
		TaxonNameTransformer transformer;
		QuerySpec qs = new QuerySpec();
		qs.sortBy("scientificNameGroup");
		extractor = new DocumentIterator<>(TAXON, qs);
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		transformer = new TaxonNameTransformer();
		BulkIndexer<ScientificNameGroup> indexer = new BulkIndexer<>(SCIENTIFIC_NAME_GROUP);
		logger.info("Loading first batch of taxa");
		List<Taxon> batch = extractor.nextBatch();
		disableAutoRefresh(SCIENTIFIC_NAME_GROUP.getIndexInfo());
		int batchNo = 0;
		while (batch != null) {
			Collection<ScientificNameGroup> scientificNameGroups = transformer.transform(batch);
			if (logger.isDebugEnabled()) {
				logger.debug("Creating/updating ScientificNameGroup documents");
			}
			indexer.index(scientificNameGroups);
			if ((++batchNo % 100) == 0) {
				logger.info("Taxa processed: {}", (batchNo * batchSize));
				logger.info("Name groups created: {}", transformer.getNumCreated());
				logger.info("Name groups updated: {}", transformer.getNumUpdated());
				logger.info("Most recent name group: {}", transformer.getLastGroup().getName());
				refreshIndex(SCIENTIFIC_NAME_GROUP.getIndexInfo());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Loading next batch of taxa");
			}
			batch = extractor.nextBatch();
		}
		indexer.index(Arrays.asList(transformer.getLastGroup()));
		setAutoRefreshInterval(SCIENTIFIC_NAME_GROUP.getIndexInfo(), "30s");
		logger.info("Taxa processed: {}", extractor.getDocCounter());
		logger.info("Name groups created: {}", transformer.getNumCreated());
		logger.info("Name groups updated: {}", transformer.getNumUpdated());
		logDuration(logger, getClass(), start);
	}

	boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	int getBatchSize()
	{
		return batchSize;
	}

	void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	int getTimeout()
	{
		return timeout;
	}

	void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

}
