package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import nl.naturalis.nba.dao.util.es.ESUtil;
import org.apache.logging.log4j.Logger;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.ConfigObject;
import org.elasticsearch.ElasticsearchStatusException;

/**
 * <p>
 * Abstract base class for objects responsible for the insertion of data into
 * ElasticSearch (a&#46;k&#46;a&#46; indexing).
 * </p>
 * 
 * @author Ayco Holleman
 * @author Tom Gilissen
 *
 * @param <T>  The type of object to be converted to and stored as a JSON
 *             document
 */
public abstract class Loader<T extends IDocumentObject> implements DocumentObjectWriter<T> {

	private static final Logger logger = getLogger(Loader.class);

	private final BulkIndexer<T> indexer;
	private final ETLStatistics stats;
	private final ArrayList<T> objs;

	private int tresh;
	private boolean suppressErrors;
	private boolean dry = ConfigObject.isEnabled(SYSPROP_DRY_RUN);

	private HashMap<String, T> idObjMap;

	/**
	 * Creates a loader for the specified document type. Indexing is triggered
	 * every time the number of objects in the loader's internal queue exceeds a
	 * certain treshold, specified by the {@code queueSize} argument. Specifying
	 * 0 (zero) for {@code queueSize} effectively disables this trigger and you
	 * <b>must</b> explicitly call {@link #flush()} yourself in order to avoid
	 * an {@link OutOfMemoryError}.
	 * 
	 * @param dt - documentType
	 * @param queueSize - queue size
	 * @param stats - ETL stats
	 */
	public Loader(DocumentType<T> dt, int queueSize, ETLStatistics stats)
	{
		this.indexer = new BulkIndexer<>(dt);
		this.tresh = queueSize;
		this.stats = stats;
		/*
		 * Make all lists slightly bigger than queueSize, because the
		 * treshold-tipping call to queue() may actually fill them beyond the
		 * treshold.
		 */
		int sz = queueSize == 0 ? 16 : queueSize + 256;
		objs = new ArrayList<>(sz);
	}

	/* (non-Javadoc)
   * @see nl.naturalis.nba.etl.DocumentObjectWriter#write(java.util.Collection)
   */
	@Override
  public final void write(Collection<T> objects)
	{
		if (objects == null || objects.size() == 0) {
			return;
		}
		objs.addAll(objects);
		if (tresh != 0 && tresh < objs.size()) {
			flush();
			ESUtil.clearCache(null);
		}
	}

	
	/**
	 * Checks if the specified id belongs to a queued object and, if so, returns
	 * the object. You must explicitly enable queue lookups by calling
	 * {@link #enableQueueLookups(boolean) enableQueueLookups}, because they
	 * require some extra internal administration.
	 * 
	 * @param id - ...
	 * @return - ...
	 */
	public T findInQueue(String id)
	{
		if (idObjMap == null) {
			throw new ETLRuntimeException("Queue lookups not enabled");
		}
		return idObjMap.get(id);
	}

	
	@Override
	public void close() throws IOException
	{
		flush();
	}

	/* (non-Javadoc)
   * @see nl.naturalis.nba.etl.DocumentObjectWriter#flush()
   */
	@Override
  public void flush() {
		if (!objs.isEmpty()) {
			try {
				if (!dry) {
					boolean indexed = false;
					int n = 1;
					while (!indexed) {
						try {
							indexer.index(objs);
							indexed = true;
						} catch (ElasticsearchStatusException e) {
							try {
								wait(100);
								ESUtil.clearCache(null);
								logger.debug("Elasticsearch is busy. Retry for attempt #{}", ++n);
							} catch (InterruptedException ex) {
								ESUtil.clearCache(null);
								logger.debug("Elasticsearch is busy. Retry for attempt #{}", ++n);
							}
							if (n == 1000) {
								logger.debug("OK, there is something wrong with elastic. After retrying a 1000 times, we give up ...");
								throw new RuntimeException("Bulk update failed: {}", e.getCause());
							}
						}
					}
				}
				stats.documentsIndexed += objs.size();
				objs.clear();
			} catch (BulkIndexException e) {
				stats.documentsRejected += e.getFailureCount();
				stats.documentsIndexed += e.getSuccessCount();
				if (!suppressErrors) {
					logger.warn(e.getMessage());
				}
			}
		}
	}
		
	/**
	 * Determines whether to suppress ERROR and WARN messages while still
	 * letting through INFO messages. This is sometimes helpful if you expect
	 * large amounts of well-known errors and warnings that just clog up your
	 * log file.
	 * 
	 * @param suppressErrors - ...
	 */
	public void suppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

	
	/**
	 * Whether or not to enable the {@link #findInQueue(String)} method.
	 * 
	 * @param enableQueueLookups
	 */
	public void enableQueueLookups(boolean enableQueueLookups)
	{
		if (enableQueueLookups) {
			idObjMap = new HashMap<>(objs.size());
		}
		else {
			idObjMap = null;
		}
	}

}
