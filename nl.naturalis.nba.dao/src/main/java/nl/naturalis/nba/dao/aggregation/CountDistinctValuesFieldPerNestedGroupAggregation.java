package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.InternalNested;
import org.elasticsearch.search.aggregations.bucket.nested.InternalReverseNested;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountDistinctValuesFieldPerNestedGroupAggregation<T extends IDocumentObject, U> extends CountDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  CountDistinctValuesFieldPerNestedGroupAggregation(DocumentType<T> dt, String field, String group,
      QuerySpec querySpec) {
    super(dt, field, group, querySpec);
  }

  @Override
  public SearchResponse executeQuery() throws InvalidQueryException {
    SearchRequestBuilder request = createSearchRequest(querySpec);
    String pathToNestedGroup = getNestedPath(group);
    int aggSize = getAggregationSize(querySpec);
    Order groupOrder = setOrdering(group, querySpec);

    AggregationBuilder fieldAgg = AggregationBuilders.reverseNested("REVERSE_NESTED_FIELD");
    CardinalityAggregationBuilder cardinalityField = AggregationBuilders.cardinality("DISTINCT_VALUES").field(field);
    fieldAgg.subAggregation(cardinalityField);
    AggregationBuilder groupAgg = AggregationBuilders.nested("NESTED_GROUP", pathToNestedGroup);
    AggregationBuilder groupTerm = AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
    groupTerm.subAggregation(fieldAgg);
    groupAgg.subAggregation(groupTerm);
    request.addAggregation(groupAgg);
    
    return executeSearchRequest(request);
  }

  @Override
  public List<Map<String, Object>> getResult() throws InvalidQueryException {

    SearchResponse response = executeQuery();
    List<Map<String, Object>> result = new LinkedList<>();
    
    InternalNested nestedGroup = response.getAggregations().get("NESTED_GROUP");
    Terms groupTerms = nestedGroup.getAggregations().get("GROUP");
    List<Bucket> buckets = groupTerms.getBuckets();
    for (Bucket bucket : buckets) {
      InternalReverseNested fields = bucket.getAggregations().get("REVERSE_NESTED_FIELD");
      InternalCardinality cardinality = fields.getAggregations().get("DISTINCT_VALUES");
      Map<String, Object> hashMap = new LinkedHashMap<>(2);
      hashMap.put(group, bucket.getKeyAsString());
      hashMap.put(field, cardinality.getValue());
      result.add(hashMap);
    }
    
    return result;
  }

}
