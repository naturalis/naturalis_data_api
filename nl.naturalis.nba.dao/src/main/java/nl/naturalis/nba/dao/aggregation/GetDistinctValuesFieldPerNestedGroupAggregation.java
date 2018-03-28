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
import org.elasticsearch.search.aggregations.bucket.nested.InternalReverseNested;
import org.elasticsearch.search.aggregations.bucket.nested.Nested;
import org.elasticsearch.search.aggregations.bucket.nested.ReverseNestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.DoubleTerms;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import org.elasticsearch.search.aggregations.bucket.terms.UnmappedTerms;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class GetDistinctValuesFieldPerNestedGroupAggregation<T extends IDocumentObject, U>
    extends GetDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  private static final Logger logger =
      getLogger(GetDistinctValuesFieldPerNestedGroupAggregation.class);

  GetDistinctValuesFieldPerNestedGroupAggregation(DocumentType<T> dt, String field, String group,
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
    SearchRequestBuilder request;
    if (querySpec != null) {
      QuerySpec querySpecCopy = new QuerySpec(querySpec);
      querySpecCopy.setSize(0);
      querySpecCopy.setFrom(0);
      request = createSearchRequest(querySpecCopy);
    } else {
      request = createSearchRequest(querySpec);
    }
    String pathToNestedGroup = getNestedPath(dt, group);
    if (from > 0)
      aggSize += from;
    Order fieldOrder = getOrdering(field, querySpec);
    Order groupOrder = getOrdering(group, querySpec);

    AggregationBuilder fieldAgg =
        AggregationBuilders.terms("FIELD").field(field).size(aggSize).order(fieldOrder);
    ReverseNestedAggregationBuilder revNestedFieldAgg =
        AggregationBuilders.reverseNested("REVERSE_NESTED_FIELD");
    revNestedFieldAgg.subAggregation(fieldAgg);
    AggregationBuilder nestedGroupAgg =
        AggregationBuilders.nested("NESTED_GROUP", pathToNestedGroup);
    AggregationBuilder groupAgg =
        AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
    groupAgg.subAggregation(revNestedFieldAgg);
    nestedGroupAgg.subAggregation(groupAgg);

    request.addAggregation(nestedGroupAgg);
    return executeSearchRequest(request);
  }

  @Override
  public List<Map<String, Object>> getResult() throws InvalidQueryException {

    logger.info("Preparing aggregation query");
    List<Map<String, Object>> result = new LinkedList<>();
    SearchResponse response = executeQuery();

    Nested nestedGroup = response.getAggregations().get("NESTED_GROUP");
    Terms groupTerms = nestedGroup.getAggregations().get("GROUP");
    List<Bucket> buckets = groupTerms.getBuckets();

    // If there are no groupTerms, we'll return a map with "null"-results
    if (buckets.size() == 0) {
      Map<String, Object> hashMap = new LinkedHashMap<>(2);
      hashMap.put(group, null);
      hashMap.put("count", 0);
      hashMap.put("values", new LinkedList<>());
      result.add(hashMap);
      return result;
    }

    int counter = 0; // The offsett
    for (Bucket bucket : buckets) {
      if (from > 0 && counter++ < from)
        continue;
      InternalReverseNested nestedField = bucket.getAggregations().get("REVERSE_NESTED_FIELD");

      List<Bucket> innerBuckets;
      if (nestedField.getAggregations().get("FIELD") instanceof StringTerms) {
        StringTerms fieldTerms = nestedField.getAggregations().get("FIELD");
        innerBuckets = fieldTerms.getBuckets();
      } else if (nestedField.getAggregations().get("FIELD") instanceof LongTerms) {
        LongTerms fieldTerms = nestedField.getAggregations().get("FIELD");
        innerBuckets = fieldTerms.getBuckets();
      } else if (nestedField.getAggregations().get("FIELD") instanceof DoubleTerms) {
        DoubleTerms fieldTerms = nestedField.getAggregations().get("FIELD");
        innerBuckets = fieldTerms.getBuckets();
      } else {
        UnmappedTerms fieldTerms = nestedField.getAggregations().get("FIELD");
        innerBuckets = fieldTerms.getBuckets();
      }

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
      hashMap.put("values", fieldTermsList);
      result.add(hashMap);
    }
    return result;
  }
}
