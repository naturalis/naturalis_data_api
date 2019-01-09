package nl.naturalis.nba.dao.util.es;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NbaException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

/**
 * Utility class for using Elasticsearch's "search after" scroll methodology.
 * The "dirty" prefix is used because this scroller iterates over "live" data,
 * meaning that if documents are deleted/created after the query is issued they
 * will be absent/present in the result set. For the NBA this is not a problem,
 * hoewever, because once the data imports complete, the data in the indices
 * does not change. Note that when using this API, the {@code from} property of
 * the {@link SearchRequest} is ignored (you can only scroll through the entire
 * result set; not jump in at some arbitrary point) and the {@code size}
 * property has a different meaning: it specifies the size of the scroll window
 * (the number of documents to fetch per scroll request); it does not specify
 * how many documents you want. The {@code DirtyScroller} class, however, still
 * allows you to specify a from and size property that work as you would expect
 * from a regular query.
 * 
 * @author Ayco Holleman
 *
 */
public class DirtyScroller implements IScroller {

	private static final Logger logger = getLogger(DirtyScroller.class);

	private DocumentType<?> dt;
	private SearchRequestBuilder request;

	private int batchSize = 10000;
	private int from = 0;
	private int size = 0;

	public DirtyScroller(QuerySpec querySpec, DocumentType<?> documentType)
			throws InvalidQueryException
	{
		this.dt = documentType;
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
		if (querySpec.getSortFields() != null) {
			logger.warn("Ignoring sort fields");
			querySpec.setSortFields(null);
		}
		QuerySpecTranslator qst = new QuerySpecTranslator(querySpec, documentType);
		request = qst.translate();
		request.addSort("_uid", SortOrder.DESC);
	}

	@Override
	public void scroll(SearchHitHandler handler) throws NbaException
	{
		request.setSize(batchSize);
		int from = this.from;
		int size = this.size;
		int to = from + size;
		int i = 0;
		String uidStart = dt.getName() + '#';
		SCROLL_LOOP: do {
			SearchResponse response = executeSearchRequest(request);
			SearchHit[] hits = response.getHits().getHits();
			if (hits.length == 0) {
				break;
			}
			if (i + hits.length < from) {
				i += hits.length;
			}
			for (SearchHit hit : hits) {
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
			String uid = uidStart + hits[hits.length - 1].getId();
			request.searchAfter(new Object[] { uid });
		} while (true);
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

}
