package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountAggregation<T extends IDocumentObject> extends AggregationQuery<T, Long> {

  private static final Logger logger = getLogger(CountAggregation.class);

  CountAggregation(DocumentType<T> dt, QuerySpec querySpec) {
    super(dt, querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing count with: ", querySpec));
    }
    SearchRequestBuilder request = createSearchRequest(querySpec);
    return executeSearchRequest(request);

  }

  @Override
  public Long getResult() throws InvalidQueryException {
    SearchResponse response = executeQuery();
    return new Long(response.getHits().getTotalHits());
  }

}
