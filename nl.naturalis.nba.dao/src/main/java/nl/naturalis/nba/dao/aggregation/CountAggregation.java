package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountAggregation<T extends IDocumentObject> extends AggregationQuery<T, Long> {

  CountAggregation(DocumentType<T> dt, QuerySpec querySpec) {
    super(dt, querySpec);
  }
  
  @Override
  public SearchResponse executeQuery() throws InvalidQueryException {
    SearchRequestBuilder request = createSearchRequest(querySpec);
    return executeSearchRequest(request); 

  }

  @Override
  public Long getResult() throws InvalidQueryException {
    SearchResponse response = executeQuery();
    return response.getHits().totalHits();
  }
    
}
