package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.toDocumentObject;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

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
 * @author Tom Gilissen
 *
 */
public class DirtyDocumentIterator<T extends IDocumentObject> implements IDocumentIterator<T> {

	private static final Logger logger = getLogger(DirtyDocumentIterator.class);
	private static final Integer DEFAULT_BATCH_SIZE = 1000;

	private final DocumentType<T> dt;
	private final QuerySpec qs;

	private Object[] lastId;   // The "search after" value
	private long size;         // Total number of documents to iterate over
	private long docCounter;   // Counts documents across batches
	private SearchHit[] batch; // The batch
	private int batchIndex;    // Index into the current batch of documents (gets reset for every new batch)
	
	private long querySize;    // querySize is either set in the QuerySpec or equals the actual querySize

	public DirtyDocumentIterator(DocumentType<T> dt)
	{
	  this(dt, new QuerySpec());
	}

	/**
	 * Creates a {@code DirtyDocumentIterator} that iterates over documents of
	 * the specified document type, satisfying the specified query
	 * specification.
	 *  
	 * Note that in order to get a consistent iteration, a sort on a field 
	 * with one unique value is needed. Because of that, the sort order from the 
	 * provided {@link QuerySpec} will be replaced with a sort on the id of the
	 * document.
	 * 
	 * @param dt
	 * @param qs
	 */
	public DirtyDocumentIterator(DocumentType<T> dt, QuerySpec querySpec)
	{
	  qs = new QuerySpec(querySpec);
	  requireNonNull(dt);
		this.dt = dt;
	}

	/**
	 * Returns the total number of documents to iterate over.
	 * 
	 * @return
	 */
	@Override
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
		if (docCounter == querySize) {
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
	 * documents to read.
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
		if (batch == null) {
			loadFirstBatch();
		}
	}

	private void loadFirstBatch()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Initializing document buffer");
		}
		setBatchSize();
		qs.setSortFields(Arrays.asList(new SortField("id")));
		SearchRequest request;
		try {
			request = new QuerySpecTranslator(qs, dt).translate();
		}
		catch (InvalidQueryException e) {
			throw new DaoException(e);
		}
		SearchResponse response = executeSearchRequest(request);
		batch = response.getHits().getHits();
		if (batch.length > 0) {
      lastId = batch[batch.length - 1].getSortValues();
		}
		size = response.getHits().getTotalHits().value;
		qs.setFrom(null); // The from-value can only be used in the first batch
		if (querySize == 0) {
		  querySize = size;
		}
	}

	private void loadNextBatch()
	{
		if (logger.isDebugEnabled()) {
			logger.debug("Refreshing document buffer");
		}
		SearchRequest request;
		try {
			request = new QuerySpecTranslator(qs, dt).translate();
		}
		catch (InvalidQueryException e) {
		  assert(false);
		  return;
		}
		SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
		searchSourceBuilder.trackTotalHits(false);
		searchSourceBuilder.searchAfter(lastId);
		request.source(searchSourceBuilder);
		
		SearchResponse response = executeSearchRequest(request);
		batch = response.getHits().getHits();
		if (batch.length > 0) {
		  lastId = batch[batch.length - 1].getSortValues();
		}
		batchIndex = 0;
	}
	
	private void setBatchSize()
	{
    if (qs.getSize() != null) {
      querySize = qs.getSize().longValue();
    } 
    qs.setSize(DEFAULT_BATCH_SIZE);
	}
	
}