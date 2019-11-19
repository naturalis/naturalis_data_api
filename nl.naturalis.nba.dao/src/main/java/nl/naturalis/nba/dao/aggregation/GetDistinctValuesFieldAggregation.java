package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationFrom;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationSize;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getOrdering;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class GetDistinctValuesFieldAggregation<T extends IDocumentObject>
    extends GetDistinctValuesAggregation<T, Map<String, Long>> {

  private static final Logger logger = getLogger(GetDistinctValuesFieldAggregation.class);

  GetDistinctValuesFieldAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, field, querySpec);
    aggSize = getAggregationSize(querySpec);
    from = getAggregationFrom(querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, querySpec));
    }
    if ((from + aggSize) > getMaxNumGroups()) {
      String fmt = "Too many groups requested. from + size must not exceed " + "%s (was %s)";
      String msg = String.format(fmt, getMaxNumGroups(), (from + aggSize));
      throw new InvalidQueryException(msg);
    }
    SearchRequest request;
    if (querySpec != null) {
      QuerySpec querySpecCopy = new QuerySpec(querySpec);
      querySpecCopy.setSize(0);
      querySpecCopy.setFrom(0);
      request = createSearchRequest(querySpecCopy);
    } else {
      request = createSearchRequest(querySpec);      
    }

    if (from > 0) aggSize += from;
    
    SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
    BucketOrder fieldOrder = getOrdering(field, querySpec);

    TermsAggregationBuilder termsAggregation = terms("FIELD");
    termsAggregation.field(field);
    termsAggregation.size(aggSize).order(fieldOrder);
    searchSourceBuilder.aggregation(termsAggregation);
    
    request.source(searchSourceBuilder);
    return executeSearchRequest(request);
  }

  @Override
  public Map<String, Long> getResult() throws InvalidQueryException {
    SearchResponse response = executeQuery();
    Terms terms = response.getAggregations().get("FIELD");

    Map<String, Long> result = new LinkedHashMap<>(terms.getBuckets().size());
    int counter = 0;
    for (Bucket bucket : terms.getBuckets()) {
      if (from > 0 && counter++ < from) continue;
      result.put(bucket.getKeyAsString(), bucket.getDocCount());
    }
    return result;
  }

}
