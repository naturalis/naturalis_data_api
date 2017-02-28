package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.dao.util.es.ESUtil;
import nl.naturalis.nba.etl.BulkIndexException;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.IOUtil;

class NameImporter {

	private static final Logger logger = getLogger(NameImporter.class);

	private boolean suppressErrors;
	private int batchSize;
	private int timeout;

	void importSpecimenNames() throws BulkIndexException
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		DocumentIterator<Specimen> extractor;
		NameTransformer transformer;
		NameLoader loader = null;
		logger.info("Initializing extractor");
		extractor = new DocumentIterator<>(SPECIMEN);
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		logger.info("Initializing transformer");
		transformer = new NameTransformer(stats, batchSize);
		transformer.setSuppressErrors(suppressErrors);
		logger.info("Initializing loader");
		loader = new NameLoader(0, stats);
		loader.suppressErrors(suppressErrors);
		List<Specimen> batch = extractor.nextBatch();
		ESUtil.disableAutoRefresh(NAME_GROUP.getIndexInfo());
		while (batch != null) {
			transformer.prepareForBatch(batch);
			for (Specimen specimen : batch) {
				transformer.transform(specimen);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Creating/updating NameGroup documents");
			}
			NameGroupUpserter.update(transformer.getNameGroups());
			if (stats.recordsProcessed % 100000 == 0) {
				logger.info("Documents processed: {}", stats.recordsProcessed);
			}
			ESUtil.refreshIndex(NAME_GROUP);
			if (logger.isDebugEnabled()) {
				logger.debug("Loading next batch of specimens");
			}
			batch = extractor.nextBatch();
		}
		ESUtil.setAutoRefreshInterval(NAME_GROUP.getIndexInfo(), "30s");
		IOUtil.close(loader);
		stats.logStatistics(logger);
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
