package nl.naturalis.nba.etl.name;

import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import static nl.naturalis.nba.etl.ETLUtil.logDuration;
import static nl.naturalis.nba.etl.LoadConstants.SYSPROP_SUPPRESS_ERRORS;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.utils.ConfigObject;
import nl.naturalis.nba.utils.IOUtil;

abstract class NameImporter<T extends IDocumentObject> {

	private static final Logger logger = getLogger(NameImporter.class);

	private final DocumentType<T> dt;
	private final boolean suppressErrors;

	NameImporter(DocumentType<T> dt)
	{
		this.dt = dt;
		suppressErrors = ConfigObject.isEnabled(SYSPROP_SUPPRESS_ERRORS);
	}

	void importNames()
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		DocumentIterator<T> extractor;
		AbstractNameTransformer<T> transformer;
		NameLoader loader = null;
		try {
			logger.info("Initializing extractor for {}", dt.getName());
			extractor = new DocumentIterator<T>(dt);
			extractor.setBatchSize(100);
			extractor.setTimeout(50000);
			loader = new NameLoader(0, stats);
			loader.suppressErrors(suppressErrors);
			transformer = createTransformer(stats);
			transformer.setSuppressErrors(suppressErrors);
			List<T> batch = extractor.nextBatch();
			logger.info("Number of {} documents to process: {}", dt.getName(), extractor.size());
			int batchNo = 0;
			while (batch != null) {
				transformer.initializeOutputObjects(batch);
				for (T inputObject : batch) {
					List<Name> outputObjects = transformer.transform(inputObject);
					loader.queue(outputObjects);
				}
				if (++batchNo % 1 == 0) {
					//logger.info("Documents processed: {}", extractor.getDocCounter());
					loader.flush();
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

	abstract AbstractNameTransformer<T> createTransformer(ETLStatistics stats);

}
