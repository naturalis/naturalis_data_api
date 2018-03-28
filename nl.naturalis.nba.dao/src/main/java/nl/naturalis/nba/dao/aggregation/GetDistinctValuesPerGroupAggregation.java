package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationFrom;
import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getAggregationSize;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public abstract class GetDistinctValuesPerGroupAggregation<T extends IDocumentObject, U>
    extends GetDistinctValuesAggregation<T, U> {

  String group;
  int aggSize;
  int from;

  GetDistinctValuesPerGroupAggregation(DocumentType<T> dt, String field, String group,
      QuerySpec querySpec) {
    super(dt, field, querySpec);
    this.group = group;
    aggSize = getAggregationSize(querySpec);
    from = getAggregationFrom(querySpec);
  }

}
