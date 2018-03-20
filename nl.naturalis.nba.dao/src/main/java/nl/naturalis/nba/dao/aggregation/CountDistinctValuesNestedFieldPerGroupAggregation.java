package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationFrom;
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
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.InternalCardinality;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountDistinctValuesNestedFieldPerGroupAggregation<T extends IDocumentObject, U>
    extends CountDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  private static final Logger logger =
      getLogger(CountDistinctValuesNestedFieldPerGroupAggregation.class);

  CountDistinctValuesNestedFieldPerGroupAggregation(DocumentType<T> dt, String field, String group,
      QuerySpec querySpec) {
    super(dt, field, group, querySpec);
    aggSize = getAggregationSize(querySpec);
    from = getAggregationFrom(querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, group, querySpec));
    }
    if ((from + aggSize) > getMaxNumGroups()) {
      String fmt = "Too many groups requested. from + size must not exceed " + "%s (was %s)";
      String msg = String.format(fmt, getMaxNumGroups(), (from + aggSize));
      throw new InvalidQueryException(msg);
    }
    QuerySpec querySpecCopy = new QuerySpec(querySpec);
    querySpecCopy.setSize(0);
    querySpecCopy.setFrom(0);
    SearchRequestBuilder request = createSearchRequest(querySpecCopy);
    String pathToNestedField = getNestedPath(dt, field);
    if (from > 0) aggSize+= from;
    Order groupOrder = getOrdering(group, querySpec);
    // Default sorting should be descending on count
    if ( groupOrder.equals(Order.count(false))) {
      groupOrder = Terms.Order.count(true);
    }

    AggregationBuilder fieldAgg = AggregationBuilders.nested("FIELD", pathToNestedField);
    CardinalityAggregationBuilder cardinalityField =
        AggregationBuilders.cardinality("DISTINCT_VALUES").field(field);
    fieldAgg.subAggregation(cardinalityField);
    AggregationBuilder groupAgg =
        AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
    groupAgg.subAggregation(fieldAgg);
    request.addAggregation(groupAgg);

    return executeSearchRequest(request);
  }

  @Override
  public List<Map<String, Object>> getResult() throws InvalidQueryException {

    SearchResponse response = executeQuery();
    List<Map<String, Object>> result = new LinkedList<>();

    Terms groupTerms = response.getAggregations().get("GROUP");
    List<Bucket> buckets = groupTerms.getBuckets();
    int counter = 0;
    for (Bucket bucket : buckets) {
      if (from > 0 && counter++ < from) continue;
      InternalNested fields = bucket.getAggregations().get("FIELD");
      InternalCardinality cardinality = fields.getAggregations().get("DISTINCT_VALUES");
      Map<String, Object> hashMap = new LinkedHashMap<>(2);
      hashMap.put(group, bucket.getKeyAsString());
      hashMap.put(field, cardinality.getValue());
      result.add(hashMap);
    }
    return result;
  }

}
