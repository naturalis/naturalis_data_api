package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.sort.SortParseElement;

import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.dao.ESClientManager;

/**
 * Utility class that provides the plumbing for search requests using
 * Elasticsearch's scroll API.
 * 
 * @author Ayco Holleman
 *
 */
public class Scroller {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(Scroller.class);

	private final SearchRequestBuilder request;
	private final SearchHitHandler handler;

	private int batchSize = 10000;
	private int timeout = 500;

	/**
	 * Creates a scroller for the specified search request, using the specified
	 * search hit handler to process the resulting documents.
	 * 
	 * @param searchRequest
	 * @param searchHitHandler
	 */
	public Scroller(SearchRequestBuilder searchRequest, SearchHitHandler searchHitHandler)
	{
		this.request = searchRequest;
		this.handler = searchHitHandler;
	}

	/**
	 * Iterates over the documents resulting from the search request. For each
	 * document the search hit handler's
	 * {@link SearchHitHandler#handle(SearchHit) handle} method is called.
	 * 
	 * @throws NbaException
	 */
	public void scroll() throws NbaException
	{
		TimeValue tv = new TimeValue(timeout);
		request.addSort(SortParseElement.DOC_FIELD_NAME, SortOrder.ASC);
		request.setScroll(new TimeValue(timeout));
		request.setSize(batchSize);
		SearchResponse response = executeSearchRequest(request);
		do {
			for (SearchHit hit : response.getHits().getHits()) {
				handler.handle(hit);
			}
			String scrollId = response.getScrollId();
			Client client = ESClientManager.getInstance().getClient();
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			response = ssrb.setScroll(tv).execute().actionGet();
		} while (response.getHits().getHits().length != 0);
	}

	/**
	 * Returns the size of the scroll window (the {@code size} property of the
	 * {@link SearchRequestBuilder}). Defaults to 10000 documents.
	 * 
	 * @return
	 */
	public int getBatchSize()
	{
		return batchSize;
	}

	/**
	 * Sets the size of the scroll window (the {@code size} property of the
	 * {@link SearchRequestBuilder}). Defaults to 10000 documents.
	 * 
	 * @param batchSize
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	/**
	 * Returns the timeout of the scroll token. Defaults to 500 milliseconds.
	 * 
	 * @return
	 */
	public int getTimeout()
	{
		return timeout;
	}

	/**
	 * Sets the timeout of the scroll token. Defaults to 500 milliseconds.
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

}
