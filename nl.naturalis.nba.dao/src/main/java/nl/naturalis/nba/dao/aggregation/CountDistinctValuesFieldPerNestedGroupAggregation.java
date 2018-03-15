package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationSize;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getNestedPath;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getOrdering;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
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

public class CountDistinctValuesFieldPerNestedGroupAggregation<T extends IDocumentObject, U>
    extends CountDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  private static final Logger logger =
      getLogger(CountDistinctValuesFieldPerNestedGroupAggregation.class);

  CountDistinctValuesFieldPerNestedGroupAggregation(DocumentType<T> dt, String field, String group,
      QuerySpec querySpec) {
    super(dt, field, group, querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, group, querySpec));
    }
    SearchRequestBuilder request = createSearchRequest(querySpec);
    String pathToNestedGroup = getNestedPath(dt, group);
    int aggSize = getAggregationSize(querySpec);
    Order groupOrder = getOrdering(group, querySpec);
    // Default sorting should be descending on count
    if ( groupOrder.equals(Order.count(false))) {
      groupOrder = Terms.Order.count(true);
    }

    AggregationBuilder fieldAgg = AggregationBuilders.reverseNested("REVERSE_NESTED_FIELD");
    CardinalityAggregationBuilder cardinalityField =
        AggregationBuilders.cardinality("DISTINCT_VALUES").field(field);
    fieldAgg.subAggregation(cardinalityField);
    AggregationBuilder groupAgg = AggregationBuilders.nested("NESTED_GROUP", pathToNestedGroup);
    AggregationBuilder groupTerm =
        AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
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
