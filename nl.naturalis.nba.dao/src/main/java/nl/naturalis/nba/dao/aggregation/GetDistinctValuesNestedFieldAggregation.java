package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static org.elasticsearch.search.aggregations.AggregationBuilders.nested;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;
import java.util.LinkedHashMap;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class GetDistinctValuesNestedFieldAggregation<T extends IDocumentObject, U>
    extends GetDistinctValuesAggregation<T, Map<String, Long>> {

  GetDistinctValuesNestedFieldAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, field, querySpec);
  }

  @Override
  public SearchResponse executeQuery() throws InvalidQueryException {

    SearchRequestBuilder request = createSearchRequest(querySpec);
    String nestedPath = getNestedPath(field);
    int aggSize = getAggregationSize(querySpec);
    Order fieldOrder = setOrdering(field, querySpec);

    TermsAggregationBuilder termsAggregation = terms("FIELD");
    termsAggregation.field(field);
    termsAggregation.size(aggSize).order(fieldOrder);

    NestedAggregationBuilder nestedAggregation = nested("NESTED_FIELD", nestedPath);
    nestedAggregation.subAggregation(termsAggregation);
    request.addAggregation(nestedAggregation);
    return executeSearchRequest(request);
  }

  @Override
  public Map<String, Long> getResult() throws InvalidQueryException {

    SearchResponse response = executeQuery();

    Nested nested = response.getAggregations().get("NESTED_FIELD");
    Terms terms = nested.getAggregations().get("FIELD");

    Map<String, Long> result = new LinkedHashMap<>(terms.getBuckets().size());
    for (Bucket bucket : terms.getBuckets()) {
      result.put(bucket.getKeyAsString(), bucket.getDocCount());
    }
    return result;
  }

}
