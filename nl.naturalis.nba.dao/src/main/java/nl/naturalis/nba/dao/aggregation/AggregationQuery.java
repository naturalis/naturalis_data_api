package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;

public abstract class AggregationQuery<T extends IDocumentObject, U> {
  
  QuerySpec querySpec;
  DocumentType<T> dt;

  AggregationQuery(DocumentType<T> dt, QuerySpec querySpec) {
    this.dt = dt;
    this.querySpec = querySpec;
  }
  
  abstract SearchResponse executeQuery() throws InvalidQueryException;

  public abstract U getResult() throws InvalidQueryException; 

  /**
   * Takes a QuerySpec and returns a SearchRequestBuilder that can be used for an
   * aggregation query.
   * 
   * @param querySpec
   * @return 
   * @throws InvalidQueryException
   */
  SearchRequestBuilder createSearchRequest(QuerySpec querySpec) throws InvalidQueryException {
    SearchRequestBuilder request;
    if (querySpec == null) {
      request = newSearchRequest(dt);
    } else {
      QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
      request = translator.translate();
    }
    request.setSize(0);
    return request;
  }
  
}
