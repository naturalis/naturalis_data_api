package nl.naturalis.nba.dao.aggregation;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.DocumentType;

public abstract class AggregationQueryFactory<T> {

  public static <T extends IDocumentObject> AggregationQuery<T, ?> createAggregationQuery(
      String type, DocumentType<T> dt, String field, String group, QuerySpec querySpec)
      throws InvalidQueryException {

    String pathToNestedField = null;
    if (field != null)
      pathToNestedField = getNestedPath(dt, field);

    String pathToNestedGroup = null;
    if (group != null)
      pathToNestedGroup = getNestedPath(dt, group);

    switch (type) {
      case "count":
        return new CountAggregation<>(dt, querySpec);

      case "getDistinctValues":
        if (pathToNestedField == null)
          return new GetDistinctValuesFieldAggregation<>(dt, field, querySpec);
        return new GetDistinctValuesNestedFieldAggregation<>(dt, field, querySpec);

      case "countDistinctValues":
        if (pathToNestedField == null)
          return new CountDistinctValuesFieldAggregation<>(dt, field, querySpec);
        return new CountDistinctValuesNestedFieldAggregation<>(dt, field, querySpec);

      case "countDistinctValuesPerGroup":
        if (pathToNestedField == null && pathToNestedGroup == null)
          return new CountDistinctValuesFieldPerGroupAggregation<>(dt, field, group, querySpec);
        else if (pathToNestedField != null && pathToNestedGroup == null)
          return new CountDistinctValuesNestedFieldPerGroupAggregation<>(dt, field, group,
              querySpec);
        else if (pathToNestedField == null && pathToNestedGroup != null)
          return new CountDistinctValuesFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);
        else
          return new CountDistinctValuesNestedFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);

      case "getDistinctValuesPerGroup":
        if (pathToNestedField == null && pathToNestedGroup == null)
          return new GetDistinctValuesFieldPerGroupAggregation<>(dt, field, group, querySpec);
        else if (pathToNestedField != null && pathToNestedGroup == null)
          return new GetDistinctValuesNestedFieldPerGroupAggregation<>(dt, field, group, querySpec);
        else if (pathToNestedField == null && pathToNestedGroup != null)
          return new GetDistinctValuesFieldPerNestedGroupAggregation<>(dt, field, group, querySpec);
        else
          return new GetDistinctValuesNestedFieldPerNestedGroupAggregation<>(dt, field, group,
              querySpec);
    }
    return null;
  }

  /**
   * Return the nested path needed for the Elasticsearch query.
   * 
   * @param dt
   * @param field
   * @return nestedPath
   * @throws InvalidQueryException
   */
  private static String getNestedPath(DocumentType<?> dt, String path)
      throws InvalidQueryException {
    MappingInfo<?> mappingInfo = new MappingInfo<>(dt.getMapping());

    String nestedPath = null;
    try {
      nestedPath = mappingInfo.getNestedPath(path);
    } catch (NoSuchFieldException e) {
      throw new InvalidQueryException(e.getMessage());
    }
    return nestedPath;
  }

}
