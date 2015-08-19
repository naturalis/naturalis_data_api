package nl.naturalis.nda.elasticsearch.load;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.naturalis.nda.elasticsearch.client.IndexNative;

import org.slf4j.Logger;

/**
 * <p>
 * Abstract base class for objects responsible for the insertion of data into
 * ElasticSearch (a.k.a. indexing). Subclasses need only implement one method:
 * {@link #getIdGenerator()}, which must extract the ElasticSearch {@code _id}
 * from the object to be stored. The assumption is that you will never want to
 * rely on ElasticSearch to generate and ID for you. If you <i>do</i> want this,
 * your implementation can and should simply return {@code null}. Subclasses may
 * also override {@link #getParentIdGenerator()} in case they need to establish
 * parent-child relationsships, but this is not required (the {@code Loader}
 * class itself already provides an implementation that just returns
 * {@code null}).
 * </p>
 * <p>
 * Once you have processed all data from all datasources that you want to index
 * using a particular writer, you SHOULD always call {@link #flush()} on that
 * instance to write any remaining objects in the writer's internal buffer to
 * ElasticSearch. You are practically guranteed to loose data if you don't call
 * {@link #flush()} when done, because the last object you added (see
 * {@link #load(List) add}) is unlikely to have triggered an automatic flush.
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

	static final Logger logger = Registry.getInstance().getLogger(ElasticSearchLoader.class);

	protected final IndexNative indexManager;
	private final String type;
	private final int treshold;
	private final ArrayList<T> objs;
	private final ArrayList<String> ids;
	private final ArrayList<String> parIds;

	private int indexed;
	private int batch;

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
	public ElasticSearchLoader(IndexNative indexManager, String documentType, int treshold)
	{
		this.indexManager = indexManager;
		this.type = documentType;
		this.treshold = treshold;
		objs = new ArrayList<>(treshold + 8);
		ids = getIdGenerator() == null ? null : new ArrayList<String>(treshold + 8);
		parIds = getParentIdGenerator() == null ? null : new ArrayList<String>(treshold + 8);
	}

	public final void load(List<T> items)
	{
		if (items == null || items.size() == 0)
			return;
		objs.addAll(items);
		if (ids != null) {
			for (T item : items) {
				ids.add(getIdGenerator().getId(item));
			}
		}
		if (parIds != null) {
			for (T item : items) {
				parIds.add(getParentIdGenerator().getParentId(item));
			}
		}
		if (objs.size() >= treshold) {
			flush();
		}
	}

	/**
	 * Get the number of documents indexed so far by this writer.
	 * 
	 * @return The number of documents indexed
	 */
	public int indexed()
	{
		return indexed;
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
	 * method explicitly as it is done implicitly by the {@link #load(List) add}
	 * methods once the size of the buffers exceeds the treshold specified in
	 * the constructor. However, you SHOULD
	 */
	public void flush()
	{
		if (!objs.isEmpty()) {
			try {
				indexManager.saveObjects(type, objs, ids, parIds);
				indexed += objs.size();
				if (++batch % 50 == 0) {
					logger.info("Documents indexed: " + indexed);
				}
			}
			finally {
				objs.clear();
				if (ids != null)
					ids.clear();
				if (parIds != null)
					parIds.clear();
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

}
