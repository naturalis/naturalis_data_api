package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountDistinctValuesFieldAggregation<T extends IDocumentObject, U> extends CountDistinctValuesAggregation<T, Long> {

  CountDistinctValuesFieldAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, field, querySpec);
  }

  @Override
  public SearchResponse executeQuery() throws InvalidQueryException {
    SearchRequestBuilder request = createSearchRequest(querySpec);
    AggregationBuilder agg = AggregationBuilders.cardinality("CARDINALITY").field(field);
    request.addAggregation(agg);
    return executeSearchRequest(request);
  }

  @Override
  public Long getResult() throws InvalidQueryException {
    long result = 0;
    SearchResponse response = executeQuery();   
    Cardinality cardinality = response.getAggregations().get("CARDINALITY");
    result = cardinality.getValue();
    return new Long(result);
  }

}
