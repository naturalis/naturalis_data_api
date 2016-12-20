package nl.naturalis.nba.etl.name;

import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Name;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.util.es.DocumentIterator;
import nl.naturalis.nba.etl.ETLRegistry;
import nl.naturalis.nba.etl.ETLStatistics;
import nl.naturalis.nba.etl.LoadConstants;
import nl.naturalis.nba.etl.LoadUtil;
import nl.naturalis.nba.utils.IOUtil;

abstract class NameImporter<T extends IDocumentObject> {

	static final Logger logger = ETLRegistry.getInstance().getLogger(NameImporter.class);

	private DocumentType<T> dt;
	private int esBulkRequestSize;

	NameImporter(DocumentType<T> dt)
	{
		this.dt = dt;
		String key = LoadConstants.SYSPROP_ES_BULK_REQUEST_SIZE;
		String val = System.getProperty(key, "1000");
		esBulkRequestSize = Integer.parseInt(val);
	}

	void importNames()
	{
		long start = System.currentTimeMillis();
		ETLStatistics stats = new ETLStatistics();
		DocumentIterator<T> extractor;
		AbstractNameTransformer<T> transformer;
		NameLoader loader = null;
		try {
			extractor = new DocumentIterator<T>(dt);
			loader = new NameLoader(esBulkRequestSize, stats);
			transformer = createTransformer(stats, loader);
			int i = 0;
			for (T specimen : extractor) {
				List<Name> names = transformer.transform(specimen);
				loader.queue(names);
				if (++i % 100000 == 0) {
					logger.info("{} documents processed: {}", dt.getName(), i);
				}
			}
		}
		catch (Throwable t) {
			logger.error(getClass().getSimpleName() + " terminated unexpectedly!", t);
		}
		finally {
			IOUtil.close(loader);
		}
		stats.logStatistics(logger);
		LoadUtil.logDuration(logger, getClass(), start);
	}

	abstract AbstractNameTransformer<T> createTransformer(ETLStatistics stats, NameLoader loader);

}
