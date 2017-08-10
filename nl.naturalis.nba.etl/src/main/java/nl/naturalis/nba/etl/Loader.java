package nl.naturalis.nba.etl;

import static nl.naturalis.nba.etl.ETLConstants.SYSPROP_DRY_RUN;
import static nl.naturalis.nba.etl.ETLUtil.getLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * <p>
 * Abstract base class for objects responsible for the insertion of data into
 * ElasticSearch (a&#46;k&#46;a&#46; indexing). Subclasses must implement only
 * one method: {@link #getIdGenerator()}, which should generate a document ID
 * for the object to be stored. The assumption is that you do not want to rely
 * on Elasticsearch to generate an ID for you. If you <i>do</i> want
 * Elasticsearch to auto-generate a document ID, your implementation should
 * simply return {@code null}. Subclasses may also override
 * {@link #getParentIdGenerator()} in case they need to establish parent-child
 * relationships, but this is not required (the {@code Loader} class itself
 * provides a default implementation that returns {@code null}).
 * </p>
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object to be converted to and stored as a JSON
 *            document
 */
public abstract class Loader<T extends IDocumentObject> implements Closeable {

	/**
	 * An interface that specifies how an ElasticSearch {@code _id} is to be
	 * extracted from the object about to be indexed.
	 * 
	 * @author Ayco Holleman
	 *
	 * @param <T>
	 *            The object to be indexed
	 */
	public interface IdGenerator<T> {

		/**
		 * Extract a document ID from the specified object.
		 * 
		 * @param obj
		 *            The object to extract the id from
		 * @return The document ID
		 */
		String getId(T obj);
	}

	/**
	 * An interface that specifies how an ElasticSearch {@code _parent} is to be
	 * extracted from the object about to be indexed.
	 * 
	 * @author Ayco Holleman
	 *
	 * @param <T>
	 *            The object to be indexed
	 */
	public interface ParentIdGenerator<T> {

		/**
		 * Extract the ID of the parent document from the specified object.
		 * 
		 * @param obj
		 *            The object to extract the parent id from
		 * @return The ID of the parent document
		 */
		String getParentId(T obj);
	}

	private static final Logger logger = getLogger(Loader.class);

	private final BulkIndexer<T> indexer;
	private final ETLStatistics stats;
	private final ArrayList<T> objs;
	private final ArrayList<String> ids;
	private final ArrayList<String> parIds;

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
	 * @param dt
	 * @param queueSize
	 * @param stats
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
		int sz = queueSize == 0 ? 256 : queueSize + 16;
		objs = new ArrayList<>(sz);
		if (getIdGenerator() == null) {
			ids = null;
		}
		else {
			ids = new ArrayList<>(sz);
		}
		if (getParentIdGenerator() == null) {
			parIds = null;
		}
		else {
			parIds = new ArrayList<>(sz);
		}
	}

	/**
	 * Adds the specified objects to a queue of objects waiting to be indexed.
	 * When the size of the queue reaches a certain treshold (specified through
	 * the {@code queueSize} parameter of the constructor), all objects in the
	 * queue are flushed at once to Elasticsearch. In other words, calling
	 * {@code queue} does not necessarily immediately trigger the specified
	 * objects to be indexed.
	 * 
	 * @param objects
	 */
	public final void queue(Collection<T> objects)
	{
		if (objects == null || objects.size() == 0) {
			return;
		}
		objs.addAll(objects);
		if (ids != null) {
			for (T item : objects) {
				ids.add(getIdGenerator().getId(item));
			}
			if (idObjMap != null) {
				for (T item : objects) {
					idObjMap.put(getIdGenerator().getId(item), item);
				}
			}
		}
		if (parIds != null) {
			for (T item : objects) {
				parIds.add(getParentIdGenerator().getParentId(item));
			}
		}
		if (tresh != 0 && tresh < objs.size()) {
			flush();
		}
	}

	/**
	 * Checks if the specified id belongs to a queued object and, if so, returns
	 * the object. You must explicitly enable queue lookups by calling
	 * {@link #enableQueueLookups(boolean) enableQueueLookups}, because they
	 * require some extra internal administration.
	 * 
	 * @param id
	 * @return
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

	/**
	 * Flushes the contents of the queue to ElasticSearch. While processing your
	 * data sources you don't have to call this method explicitly as it is done
	 * implicitly by the {@link #queue(List) queue} method once the queue fills
	 * up. However, you <b>must</b> call this method yourself (e.g. in a finally
	 * block) once all source data has been processed to make sure any remaining
	 * objects in the queue are written to Elasticsearch. Alternatively, you can
	 * set up a try-with-resources block to achieve the same.
	 */
	public void flush()
	{
		if (!objs.isEmpty()) {
			try {
				if (!dry) {
					indexer.index(objs, ids, parIds);
					stats.documentsIndexed += objs.size();
				}
			}
			catch (BulkIndexException e) {
				stats.documentsRejected += e.getFailureCount();
				stats.documentsIndexed += e.getSuccessCount();
				if (!suppressErrors) {
					logger.warn(e.getMessage());
				}
			}
			objs.clear();
			if (ids != null) {
				ids.clear();
			}
			if (parIds != null) {
				parIds.clear();
			}
			if (idObjMap != null) {
				idObjMap.clear();
			}
		}
	}

	/**
	 * Produce an object that can generate IDs for ElasticSearch documents.
	 * 
	 * @return
	 * 
	 * @see IdGenerator
	 */
	protected abstract IdGenerator<T> getIdGenerator();

	/**
	 * Produce an object that can generate parent IDs for ElasticSearch
	 * documents.
	 * 
	 * @return
	 * 
	 * @see ParentIdGenerator
	 */
	protected ParentIdGenerator<T> getParentIdGenerator()
	{
		return null;
	}

	/**
	 * Determines whether to suppress ERROR and WARN messages while still
	 * letting through INFO messages. This is sometimes helpful if you expect
	 * large amounts of well-known errors and warnings that just clog up your
	 * log file.
	 * 
	 * @param suppressErrors
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
			idObjMap = new HashMap<String, T>(objs.size());
		}
		else {
			idObjMap = null;
		}
	}

}
