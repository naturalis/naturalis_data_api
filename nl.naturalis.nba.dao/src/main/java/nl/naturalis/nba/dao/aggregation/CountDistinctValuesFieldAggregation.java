package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.util.es.ESUtil.executeSearchRequest;
import static nl.naturalis.nba.utils.debug.DebugUtil.printCall;

import org.apache.logging.log4j.Logger;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public class CountDistinctValuesFieldAggregation<T extends IDocumentObject>
    extends CountDistinctValuesAggregation<T, Long> {

  private static final Logger logger = getLogger(CountDistinctValuesFieldAggregation.class);

  CountDistinctValuesFieldAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, field, querySpec);
  }

  @Override
  SearchResponse executeQuery() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug(printCall("Executing AggregationQuery with: ", field, querySpec));
    }
    SearchRequest request = createSearchRequest(querySpec);
    SearchSourceBuilder searchSourceBuilder = (request.source() == null) ? new SearchSourceBuilder() : request.source();
    searchSourceBuilder.trackTotalHits(false);
    
    AggregationBuilder agg = AggregationBuilders.cardinality("CARDINALITY").field(field);
    searchSourceBuilder.aggregation(agg);

    request.source(searchSourceBuilder);
    return executeSearchRequest(request);
  }

  @Override
  public Long getResult() throws InvalidQueryException {
    Long result = 0L;
    SearchResponse response = executeQuery();
    Aggregations aggregations = response.getAggregations();
    Cardinality cardinality = aggregations.get("CARDINALITY");
    result = cardinality.getValue();    
    return result;
  }

}
