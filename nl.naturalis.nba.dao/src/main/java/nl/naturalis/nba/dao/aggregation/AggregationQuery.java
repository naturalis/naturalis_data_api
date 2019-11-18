package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DaoRegistry;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.translate.QuerySpecTranslator;
import nl.naturalis.nba.utils.ConfigObject;

/**
 * <p>
 * An {@code AggregationQuery} enables you to specify a query that returns not specimen or taxon
 * documents, but aggregated document information, providing you with a statistical summary of the
 * set of documents that matches the query. The set of documents you'd like to aggregate, can be
 * defined with a {@code QuerySpec}. This way, you can either aggregate all documents of a specific
 * DocumentType (using an 'empty' QuerySpec), or a limited set of documents (defined by the
 * QuerySpec you have provided).
 * </p>
 * 
 * <p>
 * An {@code AggregationQuery} needs to be created using the {@code AggregationQueryFactory} method
 * {@code createAggregationQuery(AggregationType type, DocumentType<T> dt, String field, String 
 * group, QuerySpec querySpec)}.
 * </p>
 * 
 * <p>
 * There are 5 {@code AggregationType}'s possible, each having a different return type:
 * <dl>
 * <dt>COUNT</dt>
 * <dd>provides a count of all documents : {@code<U> = Long};</dd>
 * <dt>COUNT_DISTINCT_VALUES</dt>
 * <dd>provides a count of the distinct values for a specific field : {@code<U> = Long};</dd>
 * <dt>COUNT_DISTINCT_VALUES_PER_GROUP</dt>
 * <dd>provides a count of the distinct distinct values for a specific field grouped by a second
 * field (the 'group' field) you have provided (structured as a {@code List} of {@code Maps}) :
 * {@code<U> = List<Map<String, Object>>};</dd>
 * <dt>GET_DISTINCT_VALUES</dt>
 * <dd>provides a {@code Map} containing the distinct values of the specified field and the number
 * ({@code long}) of occurences of those values : {@code<U> = Map<String, Long>};</dd>
 * <dt>GET_DISTINCT_VALUES_PER_GROUP</dt>
 * <dd>provides a {@code LIST} of {@code Map}s containing the distinct values of the specified field
 * and the number of occurences of those values, grouped by a second field (the 'group'field ) you 
 * have provided : {@code<U> = List<Map<String, Object>>}.</dd>
 * </p>
 * 
 * 
 * @author Tom Gilissen
 *
 * @param <T> : The {@code DocumentType} dt
 * @param <U> : The type of the AggregationQuery result depends on the AggregationType. It can be
 *        either {@code Long}, {@code Map<String, Long>}, or {@code List<Map<String, Object>>}.
 * 
 */
public abstract class AggregationQuery<T extends IDocumentObject, U> {

  QuerySpec querySpec;
  DocumentType<T> dt;
  
  private static final Logger logger = getLogger(AggregationQuery.class);

  AggregationQuery(DocumentType<T> dt, QuerySpec querySpec) {
    this.dt = dt;
    this.querySpec = querySpec;
  }

  abstract SearchResponse executeQuery() throws InvalidQueryException;

  public abstract U getResult() throws InvalidQueryException;

// ES 5
//  /**
//   * Takes a QuerySpec and returns a SearchRequestBuilder that can be used for an aggregation query.
//   * 
//   * @param querySpec
//   * @return
//   * @throws InvalidQueryException
//   */
//  SearchRequestBuilder createSearchRequest(QuerySpec querySpec) throws InvalidQueryException {
//    SearchRequestBuilder request;
//    if (querySpec == null) {
//      request = newSearchRequest(dt);
//    } else {
//      QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
//      request = translator.translate();
//    }
//    request.setSize(0);
//    return request;
  /**
   * Takes a QuerySpec and returns a SearchRequestBuilder that can be used for an aggregation query.
   * 
   * @param querySpec
   * @return
   * @throws InvalidQueryException
   */
  SearchRequest createSearchRequest(QuerySpec querySpec) throws InvalidQueryException {
    SearchRequest request;
    if (querySpec == null) {
      request = newSearchRequest(dt);
    } else {
      QuerySpecTranslator translator = new QuerySpecTranslator(querySpec, dt);
      request = translator.translate();  
    }
    return request;
  }
  
  protected static int getMaxNumGroups()
  {
    ConfigObject config = DaoRegistry.getInstance().getConfiguration();
    String property = "nl.naturalis.nba.aggregations.maxNumGroups";
    return config.required(property, int.class);
  }


}
