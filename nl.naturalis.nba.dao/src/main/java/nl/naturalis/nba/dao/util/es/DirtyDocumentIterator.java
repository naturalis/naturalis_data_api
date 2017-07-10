package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
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
	private SearchHit[] hits;
	// Counts documents within a batch (gets reset for every new batch)
	private int hitCounter;

	public DirtyDocumentIterator(DocumentType<T> dt)
	{
		this(dt, new QuerySpec());
	}

	public DirtyDocumentIterator(DocumentType<T> dt, QuerySpec qs)
	{
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
		if (hits.length == 0) {
			return false;
		}
		if (hitCounter == hits.length) {
			scroll();
		}
		return hits.length != 0;
	}

	@Override
	public T next()
	{
		checkReady();
		docCounter++;
		return convert(hits[hitCounter++]);
	}

	public List<T> nextBatch()
	{
		checkReady();
		if (hits.length == 0) {
			return null;
		}
		docCounter += hits.length;
		List<T> batch = new ArrayList<>(hits.length);
		for (int i = 0; i < hits.length; i++) {
			batch.add(convert(hits[i]));
		}
		scroll();
		return batch;
	}

	@Override
	public Iterator<T> iterator()
	{
		return this;
	}

	private T convert(SearchHit hit)
	{
		ObjectMapper om = dt.getObjectMapper();
		T obj = om.convertValue(hit.getSource(), dt.getJavaType());
		obj.setId(hit.getId());
		return obj;
	}

	private void checkReady()
	{
		if (!ready) {
			scroll();
			ready = true;
		}
	}

	private void scroll()
	{
		qs.setFrom(null);
		qs.setSortFields(null);
		SearchRequestBuilder request;
		try {
			request = new QuerySpecTranslator(qs, dt).translate();
		}
		catch (InvalidQueryException e) {
			throw new DaoException(e);
		}
		request.addSort("_uid", SortOrder.ASC);
		if (lastUid != null) {
			request.searchAfter(new String[] { lastUid });
		}
		SearchResponse response = executeSearchRequest(request);
		hits = response.getHits().hits();
		if (hits.length > 0) {
			lastUid = dt.getName() + '#' + hits[hits.length - 1].getId();
		}
		hitCounter = 0;
		size = response.getHits().getTotalHits();
	}

}
