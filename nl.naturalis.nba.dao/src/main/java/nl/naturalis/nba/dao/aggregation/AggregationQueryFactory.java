package nl.naturalis.nba.dao.aggregation;

import static nl.naturalis.nba.dao.aggregation.AggregationQueryUtils.getNestedPath;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;

public abstract class AggregationQueryFactory {

  public static <T extends IDocumentObject> AggregationQuery<T, ?> createAggregationQuery(
      AggregationType type, DocumentType<T> dt, String field, String group, QuerySpec querySpec)
      throws InvalidQueryException {

    String pathToNestedField = null;
    if (field != null) {
      pathToNestedField = getNestedPath(dt, field);
    }

    String pathToNestedGroup = null;
    if (group != null) {
      pathToNestedGroup = getNestedPath(dt, group);
    }

    switch (type) {
      case COUNT:
        return new CountAggregation<>(dt, querySpec);

      case COUNT_DISTINCT_VALUES:
        if (pathToNestedField == null) {
          return new CountDistinctValuesFieldAggregation<>(dt, field, querySpec);
        }
        return new CountDistinctValuesNestedFieldAggregation<>(dt, field, querySpec);

      case COUNT_DISTINCT_VALUES_PER_GROUP:
        if (pathToNestedField == null && pathToNestedGroup == null) {
          return new CountDistinctValuesFieldPerGroupAggregation<>(dt, field, group, querySpec);
        } else if (pathToNestedField != null && pathToNestedGroup == null) {
          return new CountDistinctValuesNestedFieldPerGroupAggregation<>(dt, field, group,
              querySpec);
        } else if (pathToNestedField == null && pathToNestedGroup != null) {
          return new CountDistinctValuesFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);
        } else {
          return new CountDistinctValuesNestedFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);
        }

      case GET_DISTINCT_VALUES:
        if (pathToNestedField == null) {
          return new GetDistinctValuesFieldAggregation<>(dt, field, querySpec);
        }
        return new GetDistinctValuesNestedFieldAggregation<>(dt, field, querySpec);

      case GET_DISTINCT_VALUES_PER_GROUP:
        if (pathToNestedField == null && pathToNestedGroup == null) {
          return new GetDistinctValuesFieldPerGroupAggregation<>(dt, field, group, querySpec);
        } else if (pathToNestedField != null && pathToNestedGroup == null) {
          return new GetDistinctValuesNestedFieldPerGroupAggregation<>(dt, field, group, querySpec);
        } else if (pathToNestedField == null && pathToNestedGroup != null) {
          return new GetDistinctValuesFieldPerNestedGroupAggregation<>(dt, field, group, querySpec);
        } else {
          return new GetDistinctValuesNestedFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);
        }
    }
    return null;
  }

}
