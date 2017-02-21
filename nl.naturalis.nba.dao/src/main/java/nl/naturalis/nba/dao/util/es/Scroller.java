package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.translate.SearchSpecTranslator;

/**
 * Utility class for using Elasticsearch's scroll API. Note that when using this
 * API, the {@code from} property of the {@link SearchRequest} is ignored (you
 * can only scroll through the entire result set; not jump in at some arbitrary
 * point) and the {@code size} property has a different meaning: it specifies
 * the size of the scroll window (the number of documents to fetch per scroll
 * request); it does not specify how many documents you want. The
 * {@code Scroller} class, however, still allows you to specify a from and size
 * property that work as you would expect from a regular query.
 * 
 * @author Ayco Holleman
 *
 */
public class Scroller {

	private static final Logger logger = getLogger(Scroller.class);

	private final SearchRequestBuilder request;
	private final SearchHitHandler handler;

	private int batchSize = 10000;
	private int timeout = 500;
	private int from = 0;
	private int size = 0;

	/**
	 * Creates a scroller for the specified document type. The sc
	 * 
	 * @param documentType
	 * @param searchHitHandler
	 */
	public Scroller(DocumentType<?> documentType, SearchHitHandler searchHitHandler)
	{
		this(newSearchRequest(documentType), searchHitHandler);
	}

	/**
	 * Creates a scroller for the specified search request, using the specified
	 * search hit handler to process the resulting documents. Documents will be
	 * sorted in a way that's optimal for scrolling. In other words, any sort
	 * field specified in the search request will be overwritten.
	 * 
	 * @param searchRequest
	 * @param searchHitHandler
	 */
	public Scroller(SearchRequestBuilder searchRequest, SearchHitHandler searchHitHandler)
	{
		this(searchRequest, searchHitHandler, false);
	}

	/**
	 * Creates a scroller for the specified search request, using the specified
	 * search hit handler to process the resulting documents. When specifying
	 * {@code false} for {@code keepSortOrder}, Documents will be sorted in a
	 * way that's optimal for scrolling. When specifying {@code true} the sort
	 * field, if any, in the search request will be used.
	 * 
	 * @param searchRequest
	 * @param searchHitHandler
	 * @param keepSortOrder
	 */
	public Scroller(SearchRequestBuilder searchRequest, SearchHitHandler searchHitHandler,
			boolean keepSortOrder)
	{
		request = searchRequest;
		if (!keepSortOrder) {
			request.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
		}
		handler = searchHitHandler;

	}

	public Scroller(QuerySpec querySpec, DocumentType<?> documentType,
			SearchHitHandler searchHitHandler) throws InvalidQueryException
	{
		if (querySpec.getFrom() != null) {
			from = querySpec.getFrom().intValue();
			if (logger.isDebugEnabled()) {
				logger.debug("QuerySpec property \"from\" ({}) copied and nullified", from);
			}
			querySpec.setFrom(null);
		}
		if (querySpec.getSize() != null) {
			size = querySpec.getSize().intValue();
			if (logger.isDebugEnabled()) {
				logger.debug("QuerySpec property \"size\" ({}) copied and nullified", size);
			}
			querySpec.setSize(null);
		}
		SearchSpecTranslator qst = new SearchSpecTranslator(querySpec, documentType);
		request = qst.translate();
		if (querySpec.getSortFields() == null || querySpec.getSortFields().size() == 0) {
			request.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
		}
		handler = searchHitHandler;
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
		request.setScroll(new TimeValue(timeout));
		request.setSize(batchSize);
		SearchResponse response = executeSearchRequest(request);
		int from = this.from;
		int size = this.size;
		int to = from + size;
		int i = 0;
		Client client = ESClientManager.getInstance().getClient();
		SCROLL_LOOP: do {
			if (i + response.getHits().hits().length < from) {
				i += response.getHits().hits().length;
			}
			for (SearchHit hit : response.getHits().hits()) {
				if (size != 0 && i >= to) {
					break SCROLL_LOOP;
				}
				if (i >= from) {
					if (!handler.handle(hit)) {
						break SCROLL_LOOP;
					}
				}
				i += 1;
			}
			String scrollId = response.getScrollId();
			SearchScrollRequestBuilder ssrb = client.prepareSearchScroll(scrollId);
			response = ssrb.setScroll(tv).get();
		} while (response.getHits().getHits().length != 0);
	}

	/**
	 * Returns the result set offset. Defaults to 0.
	 * 
	 * @return
	 */
	public int getFrom()
	{
		return from;
	}

	/**
	 * Sets the result set offset. Defaults to 0.
	 * 
	 * @param from
	 */
	public void setFrom(int from)
	{
		if (from < 0) {
			throw new IllegalArgumentException("from must not be negative");
		}
		this.from = from;
	}

	/**
	 * Returns the number of documents to retrieve. Defaults to 0 (all documents
	 * in the result set).
	 * 
	 * @return
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Sets the number of documents to retrieve. Specify 0 (zero) to retrieve
	 * all documents satisfying the query. Defaults to 0.
	 * 
	 * @param size
	 */
	public void setSize(int size)
	{
		if (size < 0) {
			throw new IllegalArgumentException("size must not be negative");
		}
		this.size = size;
	}

	/**
	 * Returns the size of the scroll window (the number of documents to fetch
	 * per scroll request). Defaults to 10000 documents.
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
