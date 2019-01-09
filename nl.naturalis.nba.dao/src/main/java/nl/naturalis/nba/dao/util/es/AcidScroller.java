package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

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

	private final SearchRequestBuilder request;

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
			request.addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC);
		}
	}

	@Override
	public void scroll(SearchHitHandler handler) throws NbaException
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
			// Ignore everything before the from-th document
			if (i + response.getHits().getHits().length < from) {
				i += response.getHits().getHits().length;
			}
			for (SearchHit hit : response.getHits().getHits()) {
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
