package nl.naturalis.nba.dao.aggregation;

import org.elasticsearch.search.aggregations.BucketOrder;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.dao.DocumentType;

public final class AggregationQueryUtils {

  /**
   * Aggregation Size: the value of the size parameter from the queryspec is used to set 
   * the value of the aggregation size.
   * 
   * @param querySpec
   * @return
   */
  static int getAggregationSize(QuerySpec querySpec) {
    int aggSize = 10;
    if (querySpec != null && querySpec.getSize() != null && querySpec.getSize() > 0) {
      aggSize = querySpec.getSize();
    }
    return aggSize;
  }
  
  static int getAggregationFrom(QuerySpec querySpec) {
    int from = 0;
    if (querySpec != null && querySpec.getFrom() != null && querySpec.getFrom() > 0) {
      from = querySpec.getFrom();
    }
    return from;
  }

  /**
   * Returns the nested path needed for the Elasticsearch query.
   * 
   * @param dt
   * @param field
   * @return nestedPath
   * @throws InvalidQueryException
   */
  static String getNestedPath(DocumentType<?> dt, String path)
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

  /**
   * Sorting: if the field (or group) is included as a sortField in the querySpec, 
   * then it is also used to order the aggregation result by the group terms. 
   * Otherwise, the aggregation will be ordered by descending document count.
   * 
   * @param fieldName
   * @param querySpec
   * @return order
   */
  static BucketOrder getOrdering(String fieldName, QuerySpec querySpec) {
    BucketOrder order = BucketOrder.count(false);
    if (querySpec != null && querySpec.getSortFields() != null) {
      for (SortField sortField : querySpec.getSortFields()) {
        if (sortField.getPath().equals(new SortField(fieldName).getPath())) {
          if (sortField.isAscending()) {
            order = BucketOrder.key(true);
          } else {
            order = BucketOrder.key(false);
          }
        }
      }
    }
    return order;
  }
  
  /**
   * A field used to aggregate documents must be a searchable field. This method
   * checks whether a field can be used for that purpose. It will return true 
   * when the field is a simple (searchable) field. Is the field null, it will 
   * return false. Is the field not a searchable field, the method will throw 
   * an InvalidQueryException.
   * 
   * @param dt
   * @param searchField
   * @return
   * @throws InvalidQueryException
   */
  static boolean isSimpleField(DocumentType<?> dt, String searchField) throws InvalidQueryException {
    if (searchField == null) return false;
    MappingInfo<?> info = new MappingInfo<>(dt.getMapping());
    ESField field;
    try {
       field = info.getField(searchField);
    } catch (NoSuchFieldException e) {
      throw new InvalidQueryException(e);
    }
    if (!(field instanceof SimpleField)) {
      String fmt = "Field %s cannot be queried: field is an object";
      String msg = String.format(fmt, searchField);
      throw new InvalidQueryException(msg);
    }
    return true;
  }
  
}
