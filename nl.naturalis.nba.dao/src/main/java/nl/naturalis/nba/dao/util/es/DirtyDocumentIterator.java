package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.toDocumentObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

/**
 * An {@link Iterator} implementation that iterates over Elasticsearch
 * documents.
 * 
 * @author Ayco Holleman
 *
 */
public class DirtyDocumentIterator<T extends IDocumentObject> implements IDocumentIterator<T> {

	private static final Integer DEFAULT_BATCH_SIZE = 1000;

	private final DocumentType<T> dt;
	private final QuerySpec qs;

	// The "search after" value
	private String lastUid;
	// Whether or not we've already issued our initial query request
	private boolean ready = false;
	// Total number of documents to iterate over
	private long size;
	// Counts documents across batches
	private long docCounter;
	// The batch
	private SearchHit[] batch;
	// Index into the current batch of documents (gets reset for every new batch)
	private int batchIndex;

	public DirtyDocumentIterator(DocumentType<T> dt)
	{
		this(dt, defaultQuerySpec());
	}

	public DirtyDocumentIterator(DocumentType<T> dt, QuerySpec qs)
	{
		checkQuerySpec(qs);
		this.dt = dt;
		this.qs = qs;
	}

	/**
	 * Returns the total number of documents to iterate over.
	 * 
	 * @return
	 */
	public long size()
	{
		checkReady();
		return size;
	}

	/**
	 * Returns the number of documents iterated over thus far.
	 */
	public long getDocCounter()
	{
		return docCounter;
	}

	@Override
	public boolean hasNext()
	{
		checkReady();
		if (batch.length == 0) {
			return false;
		}
		if (batchIndex == batch.length) {
			loadNextBatch();
		}
		return batch.length != 0;
	}

	@Override
	public T next()
	{
		checkReady();
		docCounter++;
		return toDocumentObject(batch[batchIndex++], dt);
	}

	/**
	 * Returns the next batch of documents or {@code null} if there are no more
	 * documents to read. The batch size is determined by the {@code size}
	 * property of the {@link QuerySpec} object passed to the two-argument
	 * constructor. If the single-argument constructor was used, the batch size
	 * will be 1000.
	 */
	public List<T> nextBatch()
	{
		checkReady();
		if (batch.length == 0) {
			return null;
		}
		docCounter += batch.length;
		List<T> docs = new ArrayList<>(batch.length);
		for (int i = 0; i < batch.length; i++) {
			docs.add(toDocumentObject(batch[i], dt));
		}
		loadNextBatch();
		return docs;
	}

	@Override
	public Iterator<T> iterator()
	{
		return this;
	}

	private void checkReady()
	{
		if (!ready) {
			loadFirstBatch();
			ready = true;
		}
	}

	private void loadFirstBatch()
	{
		qs.setSortFields(Arrays.asList(new SortField("id")));
		SearchRequestBuilder request;
		try {
			request = new QuerySpecTranslator(qs, dt).translate();
		}
		catch (InvalidQueryException e) {
			throw new DaoException(e);
		}
		SearchResponse response = executeSearchRequest(request);
		batch = response.getHits().hits();
		if (batch.length > 0) {
			lastUid = dt.getName() + '#' + batch[batch.length - 1].getId();
		}
		size = response.getHits().getTotalHits();
	}

	private void loadNextBatch()
	{
		SearchRequestBuilder request;
		try {
			request = new QuerySpecTranslator(qs, dt).translate();
		}
		catch (InvalidQueryException e) {
			throw new DaoException(e);
		}
		request.searchAfter(new String[] { lastUid });
		SearchResponse response = executeSearchRequest(request);
		batch = response.getHits().hits();
		if (batch.length > 0) {
			lastUid = dt.getName() + '#' + batch[batch.length - 1].getId();
		}
		batchIndex = 0;
	}

	private static void checkQuerySpec(QuerySpec qs)
	{
		if (!(qs.getFrom() == null || qs.getFrom() == 0)) {
			String msg = "QuerySpec's \"from\" property must be 0";
			throw new IllegalArgumentException(msg);
		}
		if (qs.getSize() == null || qs.getSize() == 0) {
			String msg = "QuerySpec's \"size\" property must not be 0";
			throw new IllegalArgumentException(msg);
		}
		if (qs.getSortFields() != null) {
			String msg = "QuerySpec's \"sortFields\" property must be null";
			throw new IllegalArgumentException(msg);
		}
	}

	private static QuerySpec defaultQuerySpec()
	{
		QuerySpec qs = new QuerySpec();
		qs.setSize(DEFAULT_BATCH_SIZE);
		return qs;
	}

}
