package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.LogicalOperator.AND;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.utils.CollectionUtil.hasElements;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import nl.naturalis.nba.api.LogicalOperator;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Utility class for the ConditionTranslator class
 */
class ConditionCollector {
  QueryCondition condition;
  MappingInfo<?> mappingInfo;
  
  ConditionCollector(QueryCondition condition, MappingInfo<?> mappingInfo) {
    this.condition = condition;
    this.mappingInfo = mappingInfo;
  }
  
  /*
   * This method takes one or more queryconditions (each of which can have a sibling condition) and 
   * returns a map in which these queryconditions (NOT the sibling conditions though) have been 
   * separated per nested path (or null, if the field in the condition doesn't have a nested path). 
   * The methods needs to know how the queryconditions are connected, so you need to supply the 
   * logical operator as well.
   */
  LinkedHashMap<String, ArrayList<QueryCondition>> createConditionsMap(LogicalOperator op) {
    
    LinkedHashMap<String, ArrayList<QueryCondition>> conditionsMap = new LinkedHashMap<>();
    List<QueryCondition> siblingConditions;
    
    if (op == AND) {
      siblingConditions = condition.getAnd();
    }
    else {
      siblingConditions = condition.getOr();
    }
    
    String nestedPath = getNestedPath(condition.getField(), mappingInfo);
    conditionsMap.putIfAbsent(nestedPath, new ArrayList<QueryCondition>());
    conditionsMap.get(nestedPath).add(condition);
    
    for (QueryCondition c : siblingConditions) {
      if (hasElements(c.getAnd()) || hasElements(c.getOr())) {
        conditionsMap.putIfAbsent(null, new ArrayList<QueryCondition>());
        conditionsMap.get(null).add(c);
      } else {
        String nestedPathNext = getNestedPath(c.getField(), mappingInfo);
        conditionsMap.putIfAbsent(nestedPathNext, new ArrayList<QueryCondition>());
        conditionsMap.get(nestedPathNext).add(c);
      }
    }
    
    return conditionsMap;
  }

}
