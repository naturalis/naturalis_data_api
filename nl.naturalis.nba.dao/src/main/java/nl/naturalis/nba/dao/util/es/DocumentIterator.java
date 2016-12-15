package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;

import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;

import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;

/**
 * An {@link Iterator} implementation that iterates over Elasticsearch
 * documents.
 * 
 * @author Ayco Holleman
 *
 */
public class DocumentIterator
		implements Iterator<Map<String, Object>>, Iterable<Map<String, Object>> {

	private int batchSize = 10000;
	private TimeValue timeout = new TimeValue(500);

	private final Client client = ESClientManager.getInstance().getClient();

	private long total;
	private long docCnt;
	private SearchHit[] hits;
	private int hitCnt;
	private String scrollId;

	public DocumentIterator(DocumentType<?> dt)
	{
		SearchRequestBuilder request = newSearchRequest(dt);
		request.addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(timeout);
		request.setSize(batchSize);
		SearchResponse response = executeSearchRequest(request);
		total = response.getHits().getTotalHits();
		scrollId = response.getScrollId();
		hits = response.getHits().hits();
	}

	@Override
	public boolean hasNext()
	{
		return docCnt < total;
	}

	@Override
	public Map<String, Object> next()
	{
		if (hitCnt == hits.length) {
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			SearchResponse response = ssrb.setScroll(timeout).get();
			scrollId = response.getScrollId();
			hits = response.getHits().hits();
			hitCnt = 0;
		}
		docCnt++;
		return hits[hitCnt++].getSource();
	}

	@Override
	public Iterator<Map<String, Object>> iterator()
	{
		return this;
	}

}
