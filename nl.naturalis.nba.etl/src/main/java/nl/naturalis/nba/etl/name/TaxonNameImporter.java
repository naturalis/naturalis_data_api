package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.TAXON;
import static nl.naturalis.nba.dao.util.es.ESUtil.disableAutoRefresh;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.setAutoRefreshInterval;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_READ_BATCH_SIZE;
import static nl.naturalis.nba.etl.ETLConstants.SYS_PROP_ENRICH_SCROLL_TIMEOUT;
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
import nl.naturalis.nba.etl.ETLRuntimeException;

class TaxonNameImporter {

	public static void main(String[] args) throws Exception
	{
		try {
			ESUtil.truncate(SCIENTIFIC_NAME_GROUP);
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
	private int readBatchSize = 512;
	private int scrollTimeout = 60000;

	void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Taxon> extractor;
		TaxonNameTransformer transformer;
		QuerySpec qs = new QuerySpec();
		qs.sortBy("acceptedName.scientificNameGroup");
		extractor = new DocumentIterator<>(TAXON, qs);
		extractor.setBatchSize(readBatchSize);
		extractor.setTimeout(scrollTimeout);
		transformer = new TaxonNameTransformer();
		BulkIndexer<ScientificNameGroup> indexer = new BulkIndexer<>(SCIENTIFIC_NAME_GROUP);
		List<Taxon> batch = extractor.nextBatch();
		disableAutoRefresh(SCIENTIFIC_NAME_GROUP.getIndexInfo());
		int batchNo = 0;
		while (batch != null) {
			Collection<ScientificNameGroup> nameGroups = transformer.transform(batch);
			if (logger.isDebugEnabled()) {
				logger.debug("Creating/updating ScientificNameGroup documents");
			}
			indexer.index(nameGroups);
			refreshIndex(SCIENTIFIC_NAME_GROUP.getIndexInfo());
			if ((++batchNo % 100) == 0) {
				logger.info("Taxa processed: {}", (batchNo * readBatchSize));
				logger.info("Name groups created: {}", transformer.getNumCreated());
				logger.info("Name groups updated: {}", transformer.getNumUpdated());
				logger.info("Most recent name group: {}", transformer.getLastGroup().getName());
			}
			if (logger.isDebugEnabled()) {
				logger.debug(">>>>>>>> Loading next batch of taxa <<<<<<<<");
			}
			batch = extractor.nextBatch();
		}
		indexer.index(Arrays.asList(transformer.getLastGroup()));
		refreshIndex(SCIENTIFIC_NAME_GROUP.getIndexInfo());
		setAutoRefreshInterval(SCIENTIFIC_NAME_GROUP.getIndexInfo(), "30s");
		logger.info("Taxa processed: {}", extractor.getDocCounter());
		logger.info("Name groups created: {}", transformer.getNumCreated());
		logger.info("Name groups updated: {}", transformer.getNumUpdated());
		logDuration(logger, getClass(), start);
	}

	public void configureWithSystemProperties()
	{
		String prop = System.getProperty(SYS_PROP_ENRICH_READ_BATCH_SIZE, "512");
		try {
			setReadBatchSize(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid read batch size: " + prop);
		}
		prop = System.getProperty(SYS_PROP_ENRICH_SCROLL_TIMEOUT, "60000");
		try {
			setScrollTimeout(Integer.parseInt(prop));
		}
		catch (NumberFormatException e) {
			throw new ETLRuntimeException("Invalid scroll timeout: " + prop);
		}
	}

	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	public int getReadBatchSize()
	{
		return readBatchSize;
	}

	public void setReadBatchSize(int readBatchSize)
	{
		this.readBatchSize = readBatchSize;
	}

	public int getScrollTimeout()
	{
		return scrollTimeout;
	}

	public void setScrollTimeout(int scrollTimeout)
	{
		this.scrollTimeout = scrollTimeout;
	}

}
