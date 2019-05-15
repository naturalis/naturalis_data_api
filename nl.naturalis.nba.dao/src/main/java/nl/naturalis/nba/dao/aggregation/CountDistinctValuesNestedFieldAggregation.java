package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getNestedPath;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountDistinctValuesNestedFieldAggregation<T extends IDocumentObject>
    extends CountDistinctValuesAggregation<T, Long> {

  private static final Logger logger = getLogger(CountDistinctValuesNestedFieldAggregation.class);

  CountDistinctValuesNestedFieldAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, field, querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, querySpec));
    }
    SearchRequestBuilder request = createSearchRequest(querySpec);
    String nestedPath = getNestedPath(dt, field);
    AggregationBuilder nested = AggregationBuilders.nested("NESTED", nestedPath);
    AggregationBuilder agg = AggregationBuilders.cardinality("CARDINALITY").field(field);
    nested.subAggregation(agg);
    request.addAggregation(nested);
    return executeSearchRequest(request);
  }

  @Override
  public Long getResult() throws InvalidQueryException {
    long result = 0;
    SearchResponse response = executeQuery();
    Nested nestedDocs = response.getAggregations().get("NESTED");
    Cardinality cardinality = nestedDocs.getAggregations().get("CARDINALITY");
    result = cardinality.getValue();
    return Long.valueOf(result);
  }

}
