package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.IOUtil;

class NameImporter {

	private static final Logger logger = getLogger(NameImporter.class);

	private boolean suppressErrors;
	private int batchSize;
	private int timeout;

	void importSpecimenNames()
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		DocumentIterator<Specimen> extractor;
		NameTransformer transformer;
		NameLoader loader = null;
		logger.info("Initializing extractor");
		extractor = new DocumentIterator<>(DocumentType.SPECIMEN);
		extractor.setBatchSize(batchSize);
		extractor.setTimeout(timeout);
		logger.info("Initializing transformer");
		transformer = new NameTransformer(stats, batchSize);
		transformer.setSuppressErrors(suppressErrors);
		logger.info("Initializing loader");
		loader = new NameLoader(0, stats);
		loader.suppressErrors(suppressErrors);
		List<Specimen> batch = extractor.nextBatch();
		while (batch != null) {
			transformer.prepareForBatch(batch);
			for (Specimen specimen : batch) {
				List<NameGroup> names = transformer.transform(specimen);
				loader.queue(names);
			}
			loader.flush();
			if (stats.recordsProcessed % 10000 == 0) {
				logger.info("Documents processed: {}", stats.recordsProcessed);
				logger.info("Documents indexed: {}", stats.documentsIndexed);
			}
			batch = extractor.nextBatch();
		}
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
