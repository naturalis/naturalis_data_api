package nl.naturalis.nba.dao.aggregation;

import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public abstract class GetDistinctValuesAggregation<T extends IDocumentObject, U> extends AggregationQuery<T, U> {
  
  String field;
  
  GetDistinctValuesAggregation(DocumentType<T> dt, String field, QuerySpec querySpec) {
    super(dt, querySpec);
    this.field = field;
  }
  
}
