package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.InternalReverseNested;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class GetDistinctValuesNestedFieldPerNestedGroupAggregation<T extends IDocumentObject, U>
    extends GetDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  private static final Logger logger =
      getLogger(GetDistinctValuesNestedFieldPerNestedGroupAggregation.class);

  public GetDistinctValuesNestedFieldPerNestedGroupAggregation(DocumentType<T> dt, String field,
      String group, QuerySpec querySpec) {
    super(dt, field, group, querySpec);
  }

  @Override
  public SearchResponse executeQuery() throws InvalidQueryException {

    SearchRequestBuilder request = createSearchRequest(querySpec);
    String pathToNestedField = getNestedPath(field);
    String pathToNestedGroup = getNestedPath(group);
    int aggSize = getAggregationSize(querySpec);
    Order fieldOrder = setOrdering(field, querySpec);
    Order groupOrder = setOrdering(group, querySpec);

    AggregationBuilder nestedFieldAgg =
        AggregationBuilders.nested("NESTED_FIELD", pathToNestedField);
    AggregationBuilder fieldAgg =
        AggregationBuilders.terms("FIELD").field(field).size(aggSize).order(fieldOrder);
    nestedFieldAgg.subAggregation(fieldAgg);
    AggregationBuilder nestedGroupAgg =
        AggregationBuilders.nested("NESTED_GROUP", pathToNestedGroup);
    AggregationBuilder groupAgg =
        AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
    ReverseNestedAggregationBuilder revNestedFieldAgg =
        AggregationBuilders.reverseNested("REVERSE_NESTED_FIELD");
    revNestedFieldAgg.subAggregation(nestedFieldAgg);
    groupAgg.subAggregation(revNestedFieldAgg);
    nestedGroupAgg.subAggregation(groupAgg);

    request.addAggregation(nestedGroupAgg);
    return executeSearchRequest(request);
  }

  public List<Map<String, Object>> getResult() throws InvalidQueryException {

    logger.info("Preparing aggregation query");
    List<Map<String, Object>> result = new LinkedList<>();
    SearchResponse response = executeQuery();

    Nested nestedGroup = response.getAggregations().get("NESTED_GROUP");
    Terms groupTerms = nestedGroup.getAggregations().get("GROUP");
    List<Bucket> buckets = groupTerms.getBuckets();
    for (Bucket bucket : buckets) {
      InternalReverseNested reverseNestedField =
          bucket.getAggregations().get("REVERSE_NESTED_FIELD");
      Nested nestedField = reverseNestedField.getAggregations().get("NESTED_FIELD");
      StringTerms fieldTerms = nestedField.getAggregations().get("FIELD");
      List<Bucket> innerBuckets = fieldTerms.getBuckets();
      List<Map<String, Object>> fieldTermsList = new LinkedList<>();
      for (Bucket innerBucket : innerBuckets) {
        Map<String, Object> aggregate = new LinkedHashMap<>(2);
        aggregate.put(field, innerBucket.getKeyAsString());
        aggregate.put("count", innerBucket.getDocCount());
        if (innerBucket.getDocCount() > 0) {
          fieldTermsList.add(aggregate);
        }
      }
      Map<String, Object> hashMap = new LinkedHashMap<>(2);
      hashMap.put(group, bucket.getKeyAsString());
      hashMap.put("count", bucket.getDocCount());
      if (fieldTermsList.size() > 0) {
        hashMap.put("values", fieldTermsList);
      }
      result.add(hashMap);
    }
    return result;
  }

}
