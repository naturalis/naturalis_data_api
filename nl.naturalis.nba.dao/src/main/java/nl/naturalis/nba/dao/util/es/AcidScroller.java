package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.ESClientManager;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

/**
 * An implementation of {@link IScroller} that uses Elasticsearch's scroll API.
 * Because the scroll API implements the equivalent of a database cursor, where
 * selected data is not affected by subsequent updates, this implementation is
 * akin to what in database land is called ACID. Note that when using the scroll
 * API, the {@code from} property of the {@link SearchRequest} is ignored (you
 * can only scroll through the entire result set; not jump in at some arbitrary
 * point) and the {@code size} property has a different meaning: it specifies
 * the size of the scroll window (the number of documents to fetch per scroll
 * request); it does not specify how many documents you want. The
 * {@code TransactionSafeScroller} class, however, still allows you to specify a
 * from and size property that work as you would expect from a regular query.
 * The advantage of using this implementation of {@link IScroller} over
 * {@link DirtyScroller} is that you can specify a sort order for the documents
 * to iterate over. However, Elasticsearch's scroll API is prone to timeouts, so
 * you must be careful to specify a generous timeout setting using
 * {@link #setTimeout(int) setTimeout}.
 * 
 * @author Ayco Holleman
 *
 */
public class AcidScroller implements IScroller {

	private static final Logger logger = getLogger(AcidScroller.class);

	private final SearchRequest request;

	private int batchSize = 1000;
	private int timeout = 500;
	private int from = 0;
	private int size = 0;

	public AcidScroller(QuerySpec querySpec, DocumentType<?> documentType)
			throws InvalidQueryException
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
		QuerySpecTranslator qst = new QuerySpecTranslator(querySpec, documentType);
		request = qst.translate();
		if (querySpec.getSortFields() == null || querySpec.getSortFields().size() == 0) {
		  SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
		  searchSourceBuilder.trackTotalHits(false);
		  searchSourceBuilder.sort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
			request.source(searchSourceBuilder);
		}
	}

	@Override
	public void scroll(SearchHitHandler handler) throws NbaException
	{
		TimeValue tv = new TimeValue(timeout);
		final Scroll scroll = new Scroll(tv);
		request.scroll(scroll);
		SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
		searchSourceBuilder.trackTotalHits(false);
		searchSourceBuilder.size(batchSize);
		request.source(searchSourceBuilder);
		
		RestHighLevelClient client = ESClientManager.getInstance().getClient();
		SearchResponse searchResponse;
    try {
      searchResponse = client.search(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      throw new NbaException(e.getMessage());
    } 
		String scrollId = searchResponse.getScrollId();
		SearchHit[] searchHits = searchResponse.getHits().getHits();

		while (searchHits != null && searchHits.length > 0) { 
		    
		    SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId); 
		    scrollRequest.scroll(scroll);
		    try {
          searchResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
          throw new NbaException(e.getMessage());
        }
		    scrollId = searchResponse.getScrollId();
		    searchHits = searchResponse.getHits().getHits();
		}

		ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
		clearScrollRequest.addScrollId(scrollId);
		ClearScrollResponse clearScrollResponse;
    try {
      clearScrollResponse = client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
      @SuppressWarnings("unused")
      boolean succeeded = clearScrollResponse.isSucceeded();		
    } catch (IOException e) {
      throw new NbaException(e.getMessage());
    }
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
