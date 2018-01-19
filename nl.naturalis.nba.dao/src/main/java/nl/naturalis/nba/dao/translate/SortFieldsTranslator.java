package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.LogicalOperator.OR;
import static nl.naturalis.nba.api.SortField.SORT_FIELD_SCORE;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import java.util.ArrayList;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortMode;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;
import org.apache.logging.log4j.Logger;

class SortFieldsTranslator {
  
  @SuppressWarnings("unused")
  private static final Logger logger = getLogger(SortFieldsTranslator.class);

  private static final org.elasticsearch.search.sort.SortOrder ES_ASC = org.elasticsearch.search.sort.SortOrder.ASC;
  private static final org.elasticsearch.search.sort.SortOrder ES_DESC = org.elasticsearch.search.sort.SortOrder.DESC;

  private static final nl.naturalis.nba.api.SortOrder NBA_ASC = nl.naturalis.nba.api.SortOrder.ASC;
  private static final nl.naturalis.nba.api.SortOrder NBA_DESC = nl.naturalis.nba.api.SortOrder.DESC;

  private QuerySpec querySpec;
  private DocumentType<?> dt;
  boolean singleCondition = false;
  


  SortFieldsTranslator(QuerySpec querySpec, DocumentType<?> documentType) {
    this.querySpec = querySpec;
    this.dt = documentType;
  }

  SortBuilder<?>[] translate() throws InvalidQueryException {
    MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());
    List<SortField> sortFields = querySpec.getSortFields();
    for (SortField sortF : sortFields)
     logger.debug("> path: " + sortF.getPath() + " sortOrder: " + sortF.getSortOrder());
    
    SortBuilder<?>[] result = new SortBuilder[sortFields.size()];
    int i = 0;
    for (SortField sf : sortFields) {
      Path path = sf.getPath();
      nl.naturalis.nba.api.SortOrder order = sf.getSortOrder();
      if (path.equals(SORT_FIELD_SCORE)) {
        ScoreSortBuilder ssb = SortBuilders.scoreSort();
        /* Default sort order DESC! */
        ssb.order(order == NBA_ASC ? ES_ASC : ES_DESC);
        result[i++] = ssb;
      } else if (path.toString().equals("id")) {
        FieldSortBuilder fsb = SortBuilders.fieldSort("_uid");
        fsb.order(order == NBA_DESC ? ES_DESC : ES_ASC);
        result[i++] = fsb;
      } else {
        FieldSortBuilder fsb = SortBuilders.fieldSort(path.toString());
        fsb.order(order == NBA_DESC ? ES_DESC : ES_ASC);
        /*
         * When sorting in ascending order on an array field, we sort on the lowest value within the
         * array. When sorting in descending order on an array field, we sort on the highest value
         * within the array.
         */
        fsb.sortMode(order == NBA_DESC ? SortMode.MAX : SortMode.MIN);
        String nestedPath;
        try {
          ESField f = mappingInfo.getField(path);
          if (!(f instanceof SimpleField)) {
            throw invalidSortField(path);
          }
          nestedPath = MappingInfo.getNestedPath(f);
        } catch (NoSuchFieldException e) {
          throw invalidSortField(sf.getPath());
        }
        logger.debug("Nested path:" + nestedPath);
        if (nestedPath != null) {
          fsb.setNestedPath(nestedPath);
          logger.debug("Nested path is set.");
          logger.debug("translateConditions(" + path + ")");
          QueryBuilder query = translateConditions(path);
          if (query != null) {
            fsb.setNestedFilter(query);
          }
        }
        result[i++] = fsb;
      }
    }
    return result;
  }

  /*
   * This method generates a "nested_filter" for a FieldSortBuilder when necessary. This is
   * necessary if: (1) the field being sorted on is in, or descends from a nested object; (2) there
   * are also query conditions on that very same field. If this is the case, those conditions must
   * be copied from the "query" section of the search request to the "sort" section of the search
   * request.
   */
  private QueryBuilder translateConditions(Path sortField) throws InvalidConditionException {

    List<QueryCondition> conditions = pruneAll(querySpec.getConditions(), sortField);
    if (conditions == null) {
      return null;
    }
    if (conditions.size() == 1) {
      QueryCondition condition = conditions.iterator().next();
      return getTranslator(condition, dt).translate();
    } 
    else if (querySpec.getLogicalOperator() == OR) {
      QueryCondition condition = conditions.iterator().next();
      for (int n = 1; n < conditions.size(); n++) {
        condition.or(conditions.get(n));
      }
      return getTranslator(condition, dt).translate();
    } 
    else {
      QueryCondition condition = conditions.iterator().next();
      for (int n = 1; n < conditions.size(); n++) {
        condition.and(conditions.get(n));
      }
      return getTranslator(condition, dt).translate();
    }
  }

  /*
   * Prune away any conditions that are not on the specified field.
   */
  private static List<QueryCondition> pruneAll(List<QueryCondition> conditions, Path sortField) {
    if (conditions == null) {
      return null;
    }
    List<QueryCondition> copies = new ArrayList<>(conditions.size());
    for (QueryCondition c : conditions) {
      c = prune(c, sortField);
      if (c != null) {
        c.setAnd(pruneAll(c.getAnd(), sortField));
        c.setOr(pruneAll(c.getOr(), sortField));
        copies.add(c);
      }
    }
    logger.debug("Size copies: " + copies.size());
    return copies.size() == 0 ? null : copies;
  }

  /*
   * Dark magic. Stay away. This method prunes away all of the specified condition's siblings that
   * are not conditions on the sort field. If the specified condition is itself not a condition on
   * the sort field, we attempt to replace it with a sibling that is. This may not always be
   * possible, in which case we return null. This basically means: we give up and the documents may
   * not be sorted correctly. We seek out a sibling that has itself zero siblings, because this
   * allows us to copy the original condition's siblings to its replacement. This way we retain as
   * many as possible conditions on the sort field while recursively descending into the original
   * condition's descendants. This is all super-hairy, but Elasticsearch is itself very vague about
   * how all this can possibly work with complex queries with deeply nested conditions on both the
   * sort field and other fields.
   */
  private static QueryCondition prune(QueryCondition condition, Path sortField) {
    if (condition.getField().equals(sortField)) {
      return condition;
    }
    QueryCondition alternative = null;
    if (condition.getAnd() != null) {
      List<QueryCondition> and = new ArrayList<>(condition.getAnd().size());
      for (QueryCondition c : condition.getAnd()) {
        if (c.getField().equals(sortField)) {
          if (alternative == null && c.getAnd() == null && c.getOr() == null) {
            alternative = c;
          } else {
            and.add(c);
          }
        }
      }
      if (alternative != null) {
        // Create copy b/c we are going to change it
        alternative = new QueryCondition(alternative);
        if (and.size() != 0) {
          alternative.setAnd(and);
        }
        if (condition.getOr() != null) {
          List<QueryCondition> or = new ArrayList<>(condition.getOr().size());
          for (QueryCondition c : condition.getOr()) {
            if (c.getField().equals(sortField)) {
              or.add(c);
            }
          }
          if (or.size() != 0) {
            alternative.setOr(or);
          }
        }
      }
    } else if (condition.getOr() != null) {
      List<QueryCondition> or = new ArrayList<>(condition.getOr().size());
      for (QueryCondition c : condition.getOr()) {
        if (c.getField().equals(sortField)) {
          if (alternative == null && c.getAnd() == null && c.getOr() == null) {
            alternative = c;
          } else {
            or.add(c);
          }
        }
      }
      if (alternative != null && or.size() != 0) {
        alternative = new QueryCondition(alternative);
        alternative.setOr(or);
      }
    }
    return alternative;
  }

  private static InvalidQueryException invalidSortField(Path field) {
    String fmt = "Invalid sort field: \"%s\"";
    String msg = String.format(fmt, field);
    return new InvalidQueryException(msg);
  }

}
