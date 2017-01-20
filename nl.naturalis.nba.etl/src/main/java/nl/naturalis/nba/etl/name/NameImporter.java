package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_LOADER_QUEUE_SIZE;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.ScientificNameSummary;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

class NameImporter {

	private static final Logger logger = getLogger(NameImporter.class);

	private final int loaderQueueSize;
	private final boolean suppressErrors;

	NameImporter()
	{
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
		String val = System.getProperty(SYSPROP_LOADER_QUEUE_SIZE, "2000");
		loaderQueueSize = Integer.parseInt(val);
	}

	void importNames()
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		DocumentIterator<Specimen> extractor;
		NameTransformer transformer;
		NameLoader loader = null;
		try {
			logger.info("Initializing extractor");
			extractor = new DocumentIterator<>(DocumentType.SPECIMEN);
			extractor.setBatchSize(2000);
			extractor.setTimeout(50000);
			logger.info("Initializing loader");
			loader = new NameLoader(loaderQueueSize, stats);
			loader.suppressErrors(suppressErrors);
			loader.enableQueueLookups(true);
			logger.info("Initializing transformer");
			transformer = new NameTransformer(stats, loader);
			transformer.setSuppressErrors(suppressErrors);
			List<Specimen> batch = extractor.nextBatch();
			while (batch != null) {
				for (Specimen specimen : batch) {
					List<ScientificNameSummary> outputObjects = transformer.transform(specimen);
					loader.queue(outputObjects);
				}
				loader.flush();
				if (stats.recordsProcessed % 50000 == 0) {
					logger.info("Documents processed: {}", stats.recordsProcessed);
					logger.info("Documents indexed: {}", stats.documentsIndexed);
				}
				batch = extractor.nextBatch();
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		finally {
			IOUtil.close(loader);
		}
		stats.logStatistics(logger);
		logDuration(logger, getClass(), start);
	}

}
