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
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.BulkIndexException;

class TaxonNameImporter {

	private static final Logger logger = getLogger(TaxonNameImporter.class);

	private boolean suppressErrors;
	private int batchSize;
	private int timeout;

	void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Taxon> extractor;
		TaxonNameTransformer transformer;
		logger.info("Initializing extractor");
		extractor = new DocumentIterator<>(TAXON, getQuerySpec());
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		logger.info("Initializing transformer");
		transformer = new TaxonNameTransformer();
		logger.info("Initializing loader");
		List<Taxon> batch = extractor.nextBatch();
		disableAutoRefresh(SCIENTIFIC_NAME_GROUP.getIndexInfo());
		int batchNo = 0;
		while (batch != null) {
			Collection<ScientificNameGroup> scientificNameGroups = transformer.transform(batch);
			if (logger.isDebugEnabled()) {
				logger.debug("Creating/updating ScientificNameGroup documents");
			}
			NameGroupUpserter.upsert(scientificNameGroups);
			if (++batchNo % 10 == 0) {
				logger.info("Taxa processed: {}", batchNo * batchSize);
				logger.info("Name groups created: {}", transformer.getNumCreated());
				refreshIndex(SCIENTIFIC_NAME_GROUP.getIndexInfo());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Loading next batch of taxa");
			}
			batch = extractor.nextBatch();
		}
		NameGroupUpserter.upsert(Arrays.asList(transformer.getLastGroup()));
		setAutoRefreshInterval(SCIENTIFIC_NAME_GROUP.getIndexInfo(), "30s");
		logger.info("Taxa processed: {}", extractor.getDocCounter());
		logger.info("Name groups created: {}", transformer.getNumCreated());
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

	private static QuerySpec getQuerySpec()
	{
		QuerySpec qs = new QuerySpec();
		qs.sortBy("acceptedName.genusOrMonomial");
		qs.sortBy("acceptedName.specificEpithet");
		qs.sortBy("acceptedName.infraspecificEpithet");
		return qs;
	}

}
