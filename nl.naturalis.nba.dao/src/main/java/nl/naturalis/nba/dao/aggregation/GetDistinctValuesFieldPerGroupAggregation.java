package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationSize;
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
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class GetDistinctValuesFieldPerGroupAggregation<T extends IDocumentObject, U>
    extends GetDistinctValuesPerGroupAggregation<T, List<Map<String, Object>>> {

  private static final Logger logger = getLogger(GetDistinctValuesFieldPerGroupAggregation.class);

  GetDistinctValuesFieldPerGroupAggregation(DocumentType<T> dt, String field, String group,
      QuerySpec querySpec) {
    super(dt, field, group, querySpec);
  }

  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, group, querySpec));
    }
    SearchRequestBuilder request = createSearchRequest(querySpec);
    int aggSize = getAggregationSize(querySpec);
    Order fieldOrder = getOrdering(field, querySpec);
    Order groupOrder = getOrdering(group, querySpec);

    AggregationBuilder fieldAgg =
        AggregationBuilders.terms("FIELD").field(field).size(aggSize).order(fieldOrder);
    AggregationBuilder groupAgg =
        AggregationBuilders.terms("GROUP").field(group).size(aggSize).order(groupOrder);
    groupAgg.subAggregation(fieldAgg);

    request.addAggregation(groupAgg);
    return executeSearchRequest(request);
  }

  public List<Map<String, Object>> getResult() throws InvalidQueryException {

    logger.info("Preparing aggregation query");
    List<Map<String, Object>> result = new LinkedList<>();
    SearchResponse response = executeQuery();

    Terms groupTerms = response.getAggregations().get("GROUP");
    List<Bucket> buckets = groupTerms.getBuckets();
    for (Bucket bucket : buckets) {
      StringTerms fieldTerms = bucket.getAggregations().get("FIELD");
      List<StringTerms.Bucket> innerBuckets = fieldTerms.getBucketsInternal();
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
