package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

class NameImporter {

	static int BATCH_SIZE = 500;

	private static final Logger logger = getLogger(NameImporter.class);

	private final boolean suppressErrors;

	NameImporter()
	{
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
	}

	void importNames()
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		stats.setOneToMany(true);
		DocumentIterator<Specimen> extractor;
		NameTransformer transformer;
		NameLoader loader = null;
		logger.info("Initializing extractor");
		extractor = new DocumentIterator<>(DocumentType.SPECIMEN);
		extractor.setBatchSize(BATCH_SIZE);
		extractor.setTimeout(50000);
		logger.info("Initializing transformer");
		transformer = new NameTransformer(stats);
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
			if (stats.recordsProcessed % 100000 == 0) {
				logger.info("Documents processed: {}", stats.recordsProcessed);
				logger.info("Documents indexed: {}", stats.documentsIndexed);
			}
			batch = extractor.nextBatch();
		}
		IOUtil.close(loader);
		stats.logStatistics(logger);
		logDuration(logger, getClass(), start);
	}

}
