package nl.naturalis.nba.etl;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nba.etl.col.CoLReferenceImporter;
import nl.naturalis.nba.etl.col.CoLSynonymImporter;
import nl.naturalis.nba.etl.col.CoLTaxonImporter;
import nl.naturalis.nba.etl.col.CoLVernacularNameImporter;
import nl.naturalis.nba.etl.elasticsearch.BulkIndexException;
import nl.naturalis.nba.etl.elasticsearch.IndexManagerNative;

import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Abstract base class for objects responsible for the insertion of data into
 * ElasticSearch (a.k.a. indexing). Subclasses must implement only one method:
 * {@link #getIdGenerator()}, which must extract the ElasticSearch {@code _id}
 * from the object to be stored. The assumption is that you will never want to
 * rely on ElasticSearch to generate an ID for you. If you <i>do</i> want this,
 * your implementation can and should simply return {@code null}. Subclasses may
 * also override {@link #getParentIdGenerator()} in case they need to establish
 * parent-child relationships, but this is not required (the {@code Loader}
 * class itself already provides a default implementation that just returns
 * {@code null}).
 * </p>
 * <p>
 * Once you have processed all data from all datasources that you want to index
 * using a particular writer, you <b>should</b> always call {@link #flush()} on
 * that instance to write any remaining objects in the writer's internal buffer
 * to ElasticSearch. You are practically guranteed to loose data if you don't
 * call {@link #flush()} when done, because the last object you added (see
 * {@link #load(List) add}) is unlikely to trigger an automatic flush.
 * </p>
 * 
 * @author Ayco Holleman
 *
 * @param <T>
 *            The type of object to be converted to and stored as a JSON
 *            document
 */
public abstract class ElasticSearchLoader<T> implements Closeable {

	/**
	 * An interface that specifies how an ElasticSearch {@code _id} is to be
	 * extracted from the object about to be indexed.
	 * 
	 * @author Ayco Holleman
	 *
	 * @param <T>
	 *            The object to be indexed
	 */
	public static interface IdGenerator<T> {

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
	public static interface ParentIdGenerator<T> {

		/**
		 * Extract the ID of the parent document from the specified object.
		 * 
		 * @param obj
		 *            The object to extract the parent id from
		 * @return The ID of the parent document
		 */
		String getParentId(T obj);
	}

	private final Logger logger = Registry.getInstance().getLogger(getClass());

	private final IndexManagerNative idxMgr;
	private final String type;
	private final int treshold;
	private final ETLStatistics stats;

	private final ArrayList<T> objs;
	private final ArrayList<String> ids;
	private final ArrayList<String> parIds;

	private int batch;
	private boolean suppressErrors;

	/**
	 * Create a loader that uses the specified index manager for bulk-indexing
	 * documents of the specified document type. Indexing is triggered every
	 * time the number of objects added to the loader via the
	 * {@link #load(List)} operations exceeds the specified treshold.
	 * 
	 * @param indexManager
	 * @param documentType
	 * @param treshold
	 */
	public ElasticSearchLoader(IndexManagerNative indexManager, String documentType, int treshold,
			ETLStatistics stats)
	{
		this.idxMgr = indexManager;
		this.type = documentType;
		this.treshold = treshold;
		this.stats = stats;
		/*
		 * Make all lists a bit bigger than the treshold, because the
		 * treshold-tipping call to load() may actually exceed it.
		 */
		objs = new ArrayList<>(treshold + 16);
		if (getIdGenerator() != null)
			ids = new ArrayList<>(treshold + 16);
		else
			ids = null;
		if (getParentIdGenerator() == null)
			parIds = null;
		else
			parIds = new ArrayList<>(treshold + 16);
	}

	/**
	 * Adds the specified objects to a queue of to-be-indexed objects. When the
	 * size of the queue reaches the treshold, all objects in the queue are
	 * flushed at once to ElasticSearch. In other words, calling {@code load}
	 * does not necessarily immediately trigger the specified objects to be
	 * indexed. The specified list of object is most likely retrieved from a
	 * call to {@link Transformer#transform(Object)}, which is allowed to return
	 * an empty list or {@code null} if no output can or should be produced from
	 * the input object. Therefore, this method explicitly accepts empty lists
	 * and {@code null} arguments (resulting in a no-op).
	 * 
	 * @param objects
	 */
	public final void load(List<T> objects)
	{
		if (objects == null || objects.size() == 0)
			return;
		objs.addAll(objects);
		if (ids != null) {
			for (T item : objects) {
				ids.add(getIdGenerator().getId(item));
			}
		}
		if (parIds != null) {
			for (T item : objects) {
				parIds.add(getParentIdGenerator().getParentId(item));
			}
		}
		if (objs.size() >= treshold)
			flush();
	}

	/**
	 * Checks if the specified id belongs to an object that is about to be
	 * indexed and, if so, returns the object. This functionality is needed for
	 * import programs that enrich existing documents rather than creating new
	 * ones. This applies to all CoL import programs except the
	 * {@link CoLTaxonImporter taxon importer}. The {@link CoLSynonymImporter
	 * synonym importer}, {@link CoLReferenceImporter literature reference
	 * importer} and {@link CoLVernacularNameImporter vernacular name importer}
	 * all enrich taxon documents rather than creating their own type of
	 * documents.
	 * 
	 * @param id
	 * @return
	 */
	public T findInQueue(String id)
	{
		assert (ids != null);
		int i;
		for (i = 0; i < ids.size(); ++i) {
			if (ids.get(i).equals(id)) {
				return objs.get(i);
			}
		}
		return null;
	}

	/**
	 * Just calls {@link #flush()} so that a try-with-resources instantiation of
	 * this writer is guaranteed to flush the object buffer for you.
	 */
	@Override
	public void close() throws IOException
	{
		flush();
	}

	/**
	 * Flushes the contents of the internal object buffer to ElasticSearch.
	 * While in the midst of processing your data you don't have to call this
	 * method explicitly as it is done implicitly by the {@link #load(List)
	 * load} method once the size of the buffers exceeds the treshold specified
	 * in the constructor. However, you must call this method yourself once all
	 * records have been processed to make sure any remaining objects in the
	 * buffer are written to ElasticSearch. (Alternatively, you can set up a
	 * try-with-resource block to achieve the same.)
	 */
	public void flush()
	{
		if (!objs.isEmpty()) {
			try {
				idxMgr.saveObjects(type, objs, ids, parIds);
				stats.documentsIndexed += objs.size();
			}
			catch (BulkIndexException e) {
				stats.documentsRejected += e.getFailureCount();
				stats.documentsIndexed += e.getSuccessCount();
				if (!suppressErrors)
					logger.warn(e.getMessage());
			}
			finally {
				objs.clear();
				if (ids != null)
					ids.clear();
				if (parIds != null)
					parIds.clear();
			}
			if (++batch % 50 == 0) {
				logger.info("Documents indexed: " + stats.documentsIndexed);
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
	 * Whether or not ERROR and WARN messages are suppressed.
	 * 
	 * @return
	 */
	public boolean isSuppressErrors()
	{
		return suppressErrors;
	}

	/**
	 * Determines whether to suppress ERROR and WARN messages, while still
	 * letting through INFO messages. This may be helpful if you expect large
	 * amounts of well-known ERROR messages that just clog up your log file.
	 * 
	 * @param suppressErrors
	 */
	public void setSuppressErrors(boolean suppressErrors)
	{
		this.suppressErrors = suppressErrors;
	}

}
