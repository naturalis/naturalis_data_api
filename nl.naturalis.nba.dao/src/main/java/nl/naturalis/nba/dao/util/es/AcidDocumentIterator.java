package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.exception.DaoException;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

/**
 * An {@link Iterator} implementation that iterates over Elasticsearch
 * documents.
 * 
 * @author Ayco Holleman
 *
 */
public class AcidDocumentIterator<T extends IDocumentObject> implements IDocumentIterator<T> {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(AcidDocumentIterator.class);

	private static final int DEFAULT_TIMEOUT = 30000; // msec
	private static final int DEFAULT_BATCH_SIZE = 100;

	private final RestHighLevelClient client;

	private final DocumentType<T> dt;
	private final QuerySpec qs;

	/* Fields determining scroll behaviour */
	private TimeValue timeout;
	private int batchSize;
	private String scrollId;

	// Whether or not we've already issued our initial query request
	private boolean ready = false;
	// Number of documents of the requested document type
	private long size;
	// Counts documents across batches
	private long docCounter;
	// The batch
	private SearchHit[] hits;
	// Counts documents within a batch (gets reset for every new batch)
	private int hitCounter;

	public AcidDocumentIterator(DocumentType<T> dt)
	{
		this(dt, null);
	}

	public AcidDocumentIterator(DocumentType<T> dt, QuerySpec qs)
	{
		this.client = ESClientManager.getInstance().getClient();
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

	@Override
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

	@Override
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
		T obj = om.convertValue(hit.getSourceAsString(), dt.getJavaType());
		obj.setId(hit.getId());
		return obj;
	}

	public TimeValue getTimeout()
	{
		return timeout;
	}

	public void setTimeout(TimeValue timeout)
	{
		this.timeout = timeout;
	}

	public void setTimeout(int milliseconds)
	{
		this.timeout = new TimeValue(milliseconds);
	}

	public int getBatchSize()
	{
		return batchSize;
	}

	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	private void checkReady()
	{
		if (!ready) {
			if (timeout == null) {
				timeout = new TimeValue(DEFAULT_TIMEOUT);
			}
			if (batchSize == 0) {
				batchSize = DEFAULT_BATCH_SIZE;
			}
			SearchRequest request;
			if (qs == null) {
				request = newSearchRequest(this.dt);
				SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
				searchSourceBuilder.sort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
				request.source(searchSourceBuilder);
			}
			else {
				try {
					request = new QuerySpecTranslator(qs, dt).translate();
				}
				catch (InvalidQueryException e) {
					throw new DaoException(e);
				}
			}
			request.scroll(timeout);
			SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
			searchSourceBuilder.size(batchSize);
			request.source(searchSourceBuilder);
			SearchResponse response = executeSearchRequest(request);
			size = response.getHits().getTotalHits().value;
			scrollId = response.getScrollId();
			hits = response.getHits().getHits();
			ready = true;
		}
	}

	private void scroll()
	{
	  // ES 5
//		SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId); 
//		SearchResponse response = ssrb.setScroll(timeout).get();
//		scrollId = response.getScrollId();
//		hits = response.getHits().getHits();
//		hitCounter = 0;
	  
    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
    scrollRequest.scroll(timeout);
    SearchResponse response;
    try {
      response = client.scroll(scrollRequest, RequestOptions.DEFAULT);
      scrollId = response.getScrollId();
      hits = response.getHits().getHits();
      hitCounter = 0;
    } catch (IOException e) {
      // TODO Auto-generated catch block
      // e.printStackTrace();
      throw new DaoException(e.getMessage());
    }

	  
	}

}
