package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.disableAutoRefresh;
import static nl.naturalis.nba.dao.util.es.ESUtil.refreshIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.setAutoRefreshInterval;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.BulkIndexException;

class SpecimenNameImporter {

	private static final Logger logger = getLogger(SpecimenNameImporter.class);

	private boolean suppressErrors;
	private int batchSize;
	private int timeout;

	void importNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		DocumentIterator<Specimen> extractor;
		SpecimenNameTransformer transformer;
		logger.info("Initializing extractor");
		extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		logger.info("Initializing transformer");
		transformer = new SpecimenNameTransformer(batchSize);
		logger.info("Initializing loader");
		List<Specimen> batch = extractor.nextBatch();
		disableAutoRefresh(NAME_GROUP.getIndexInfo());
		int batchNo = 0;
		while (batch != null) {
			Collection<NameGroup> nameGroups = transformer.transform(batch);
			if (logger.isDebugEnabled()) {
				logger.debug("Creating/updating NameGroup documents");
			}
			NameGroupUpserter.upsert(nameGroups);
			if (batchNo++ % 10 == 0) {
				logger.info("Specimens processed: {}", batchNo * batchSize);
				logger.info("Name groups created: {}", transformer.getNumCreated());
				logger.info("Name groups updated: {}", transformer.getNumUpdated());
				refreshIndex(NAME_GROUP.getIndexInfo());
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Loading next batch of specimens");
			}
			batch = extractor.nextBatch();
		}
		setAutoRefreshInterval(NAME_GROUP.getIndexInfo(), "30s");
		logger.info("Specimens processed: {}", extractor.getDocCounter());
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
