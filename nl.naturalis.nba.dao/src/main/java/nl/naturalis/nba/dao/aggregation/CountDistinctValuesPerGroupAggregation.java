package nl.naturalis.nba.dao.aggregation;

import java.util.List;
import java.util.Map;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public abstract class CountDistinctValuesPerGroupAggregation<T extends IDocumentObject, U> extends CountDistinctValuesAggregation<T, List<Map<String, Object>>> {

  String group;
  
  CountDistinctValuesPerGroupAggregation(DocumentType<T> dt, String field, String group, QuerySpec querySpec) {
    super(dt, field, querySpec);
    this.group = group;
  }

}