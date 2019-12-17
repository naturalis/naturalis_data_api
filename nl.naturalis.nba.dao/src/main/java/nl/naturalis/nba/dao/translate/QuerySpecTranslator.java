package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.common.json.JsonUtil.toPrettyJson;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.DaoUtil.prune;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static nl.naturalis.nba.dao.util.es.ESUtil.newCountRequest;
import static nl.naturalis.nba.dao.util.es.ESUtil.newSearchRequest;
import static nl.naturalis.nba.utils.ArrayUtil.stringify;
import static nl.naturalis.nba.utils.CollectionUtil.hasElements;

import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;

/**
 * A {@code QuerySpecTranslator} translates a {@link QuerySpec} object into an Elasticsearch
 * {@link SearchRequestBuilder query}.
 * 
 * @author Ayco Holleman
 *
 */
public class QuerySpecTranslator {

  private static final Logger logger = getLogger(QuerySpecTranslator.class);
  private static final int DEFAULT_SIZE = 10;

  private QuerySpec spec;
  private DocumentType<?> dt;

  /**
   * Creates a translator for the specified {@link QuerySpec} object generating a query for the
   * specified document type.
   * 
   * @param querySpec
   * @param documentType
   */
  public QuerySpecTranslator(QuerySpec querySpec, DocumentType<?> documentType) {
    this.spec = querySpec;
    this.dt = documentType;
  }

  /**
   * Translates the {@link QuerySpec} object into an Elasticsearch query.
   * 
   * @return
   * @throws InvalidQueryException
   * @throws NoSuchFieldException
   */
  public SearchRequest translate() throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug("Translating QuerySpec:\n{}", toPrettyJson(prune(spec)));
    }
    SearchRequest request = newSearchRequest(dt);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    
    // Set to true the search response will always track the number of hits that match the query accurately
    searchSourceBuilder.trackTotalHits(true);
    
    if (spec.getConditions() != null && !spec.getConditions().isEmpty()) {
      overrideNonScoringIfNecessary();
      QueryBuilder query;
      if (spec.isConstantScore()) {
        query = constantScoreQuery(translateConditions());
      } else {
        query = translateConditions();
      }
      searchSourceBuilder.query(query);
    }
    
    if (spec.getFields() != null) {
      if (spec.getFields().isEmpty()) {
        searchSourceBuilder.fetchSource(false);
      } else {
        addFields(searchSourceBuilder);
      }
    }
    
    searchSourceBuilder.from(spec.getFrom() == null ? 0 : spec.getFrom());
    searchSourceBuilder.size(spec.getSize() == null ? DEFAULT_SIZE : spec.getSize());

    if (spec.getSortFields() != null) {
      SortFieldsTranslator sfTranslator = new SortFieldsTranslator(spec, dt);
      for (SortBuilder<?> sortBuilder : sfTranslator.translate()) {
        searchSourceBuilder.sort(sortBuilder);
      }
    }
    
    request.source(searchSourceBuilder);
    return request;
  }

  public SearchRequest translate(boolean fetchSource) throws InvalidQueryException {
    if (logger.isDebugEnabled()) {
      logger.debug("Translating QuerySpec:\n{}", toPrettyJson(prune(spec)));
    }
    SearchRequest request = newSearchRequest(dt);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.trackTotalHits(true);
    
    if (spec.getConditions() != null && !spec.getConditions().isEmpty()) {
      overrideNonScoringIfNecessary();
      QueryBuilder query;
      if (spec.isConstantScore()) {
        query = constantScoreQuery(translateConditions());
      } else {
        query = translateConditions();
      }
      searchSourceBuilder.query(query);
    }
    
    if (spec.getFields() != null) {
      if (spec.getFields().isEmpty()) {
        searchSourceBuilder.fetchSource(false);
      } else {
        addFields(searchSourceBuilder);
      }
    }
    
    searchSourceBuilder.from(spec.getFrom() == null ? 0 : spec.getFrom());
    searchSourceBuilder.size(spec.getSize() == null ? DEFAULT_SIZE : spec.getSize());
    searchSourceBuilder.fetchSource(fetchSource);

    if (spec.getSortFields() != null) {
      SortFieldsTranslator sfTranslator = new SortFieldsTranslator(spec, dt);
      for (SortBuilder<?> sortBuilder : sfTranslator.translate()) {
        searchSourceBuilder.sort(sortBuilder);
      }
    }
    
    request.source(searchSourceBuilder);
    return request;
  }
  
  /**
   * A CountRequest does not allow "from" and "size". It only use the conditions
   * of the query.
   * 
   * @return CountRequest
   * @throws InvalidConditionException
   */
  public CountRequest translateCountRequest() throws InvalidConditionException {
    if (logger.isDebugEnabled() && spec != null) {
      logger.debug("Translating QuerySpec to CountRequest:\n{}", toPrettyJson(prune(spec)));
    }
    CountRequest request = newCountRequest(dt);
    if (spec!= null && spec.getConditions() != null && !spec.getConditions().isEmpty()) {
      QueryBuilder query = translateConditions();
      request.query(query);
    }
    return request;
  }
  
  private void addFields(SearchSourceBuilder sourceBuilder) throws InvalidQueryException {
    MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
    List<Path> fields = spec.getFields();
    for (Path field : fields) {
      if (field.toString().equals("id")) {
        /*
         * This is a special field that can be used to retrieve the Elasticsearch document ID, which
         * is not part of the document itself, but it IS an allowed field, populated through
         * SearchHit.getId() rather than through document data.
         */
        continue;
      }
      try {
        mappingInfo.getField(field);
      } catch (NoSuchFieldException e) {
        throw new InvalidQueryException(e.getMessage());
      }
    }
    String[] include = stringify(fields);
    sourceBuilder.fetchSource(include, null);
  }
  
  private QueryBuilder translateConditions() throws InvalidConditionException {
    List<QueryCondition> conditions = spec.getConditions();
    if (conditions.size() == 1) {
      QueryCondition condition = conditions.iterator().next();
      // Check for a single condition
      if (!hasElements(condition.getOr()) && !hasElements(condition.getAnd())) {
        return getTranslator(condition, dt).singleCondition().translate();
      }
      return getTranslator(condition, dt).translate();
    } else if (spec.getLogicalOperator() == OR) {
      QueryCondition qc = new QueryCondition(false);
      for(QueryCondition c : conditions) {
        qc.or(c);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Rewritten query to:\n" + JsonUtil.toPrettyJson(qc));        
      }
      return getTranslator(qc, dt).translate();
    } else {
      QueryCondition qc = new QueryCondition(true);
      for(QueryCondition c :conditions) {
        qc.and(c);
      }
      if (logger.isDebugEnabled()) {
        logger.debug("Rewritten query to:\n" + JsonUtil.toPrettyJson(qc));        
      }
      return getTranslator(qc, dt).translate();
    }
  }

  /*
   * This will set the nonScoring field of individual conditions within a QuerySpec to false if the
   * QuerySpec as a whole is non-scoring or if the condition is negated. If we are dealing with a
   * non-scoring search the Elasticsearch query generated from all conditions together is wrapped
   * into one big constant_score query. The queries generated from the individual conditions should
   * then not also be wrapped into a constant_score query. Negated conditions are intrinsically
   * non-scoring, so do not need to be wrapped into a constant_score query.
   */
  private void overrideNonScoringIfNecessary() {
    if (spec.isConstantScore()) {
      for (QueryCondition c : spec.getConditions()) {
        resetToScoring(c);
      }
    }
  }

  private static void resetToScoring(QueryCondition condition) {
    if (condition.isConstantScore()) {
      condition.setConstantScore(false);
      if (logger.isDebugEnabled()) {
        String field = condition.getField().toString();
        String msg = "constantScore field for Condition on field {} "
            + "reset to false because one of its ancestors "
            + "already has its constantScore attribute set to true";
        logger.debug(msg, field);
      }
    }
    if (condition.getAnd() != null) {
      for (QueryCondition c : condition.getAnd()) {
        resetToScoring(c);
      }
    }
    if (condition.getOr() != null) {
      for (QueryCondition c : condition.getOr()) {
        resetToScoring(c);
      }
    }
  }

}
