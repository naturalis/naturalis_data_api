package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;

import java.util.Iterator;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;

/**
 * An {@link Iterator} implementation that iterates over Elasticsearch
 * documents.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentIterator<T extends IDocumentObject> implements Iterator<T>, Iterable<T> {

	private final Client client;
	private final DocumentType<T> dt;

	private TimeValue timeout = new TimeValue(500);
	private int batchSize = 10000;

	private boolean ready = false;
	private long size;
	private long docCounter;
	private SearchHit[] hits;
	private int hitCounter;
	private String scrollId;

	public DocumentIterator(DocumentType<T> dt)
	{
		this.client = ESClientManager.getInstance().getClient();
		this.dt = dt;
	}

	public long size()
	{
		checkReady();
		return size;
	}

	@Override
	public boolean hasNext()
	{
		checkReady();
		return docCounter < size;
	}

	@Override
	public T next()
	{
		checkReady();
		if (hitCounter == hits.length) {
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			SearchResponse response = ssrb.setScroll(timeout).get();
			scrollId = response.getScrollId();
			hits = response.getHits().hits();
			hitCounter = 0;
		}
		docCounter++;
		return newDocumentObject(hits[hitCounter++]);
	}

	@Override
	public Iterator<T> iterator()
	{
		return this;
	}

	private T newDocumentObject(SearchHit hit)
	{
		ObjectMapper om = dt.getObjectMapper();
		T documentObject = om.convertValue(hit.getSource(), dt.getJavaType());
		documentObject.setId(hit.getId());
		return documentObject;
	}

	private void checkReady()
	{
		if (!ready) {
			SearchRequestBuilder request = newSearchRequest(this.dt);
			request.addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC);
			request.setScroll(timeout);
			request.setSize(batchSize);
			SearchResponse response = executeSearchRequest(request);
			size = response.getHits().getTotalHits();
			scrollId = response.getScrollId();
			hits = response.getHits().hits();
			ready = true;
		}
	}

}
