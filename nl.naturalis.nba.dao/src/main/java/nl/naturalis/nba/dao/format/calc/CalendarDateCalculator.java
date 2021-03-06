package nl.naturalis.nba.dao.format.calc;

import static java.lang.String.format;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.InvalidPathException;
import nl.naturalis.nba.common.PathUtil;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * CalendarDateCalculator is a calculator that converts the value from a DateTime field to a
 * calendar date (YYYY-MM-DD). The calculator will return a datum in the format 'yyyy-MM-dd'.
 * 
 * Note that the date field may be part of an array or child document. This calculator will return
 * only the value of the date field of the first item / first child. When the field is even further
 * nested, this calculator will only return the value of the first of the first, ...., of the first
 * item.
 *
 */
public class CalendarDateCalculator implements ICalculator {

  private PathValueReader pathValueReader;

  private static Logger logger = LogManager.getLogger(CalendarDateCalculator.class);

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args)
      throws CalculatorInitializationException {

    String dateField = args.get(null);
    Path path = new Path(getPathToField(docType, dateField));
    try {
      PathUtil.validate(path, MappingFactory.getMapping(docType));
    } catch (InvalidPathException e) {
      String msg = format("Entity %s: %s", dateField, e.getMessage());
      throw new CalculatorInitializationException(msg);
    }
    pathValueReader = new PathValueReader(path);
  }

  @Override
  public String calculateValue(EntityObject entity) throws CalculationException {
    try {
      Object obj = pathValueReader.read(entity.getDocument());
      if (obj != null) {
        String str = obj.toString();
        DateTime dt = new DateTime(str).withZone(DateTimeZone.UTC);
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        return dt.toString(fmt);
      }
    } catch (InvalidPathException e1) {
      // A check for the validity of this field has been done in the initialiser,
      // so we do not need have to deal with errors here again.
      logger.error(e1.getMessage());
    } catch (IllegalArgumentException e2) {
      // Should not be possible, but anyway ...
      logger.error("Record contains illegal date value: " + e2.getMessage());
    }
    return "";
  }

  /**
   * getPathToField will return a the full path to the first instance of the given field. E.g.:
   * identifications.defaultClassification.subgenus will be converted to
   * identifications.0.defaultClassification.subgenus This is because identifications is of type
   * array and we're only interested in the first item of the array.
   * 
   * @param docType
   * @param field
   * @return Path
   */
  private static Path getPathToField(Class<? extends IDocumentObject> docType, String field) {
    MappingInfo<? extends IDocumentObject> mapping =
        new MappingInfo<>(MappingFactory.getMapping(docType));
    try {
      Path parent = new Path(mapping.getNestedPath(field));
      if (PathUtil.isArray(parent, MappingFactory.getMapping(docType))) {
        // path = fieldname - parent
        String path = field.substring(parent.toString().length() + 1);
        return getPathToField(docType, parent.toString()).append("0").append(path);
      }
      // path = fieldname - parent
      String path = field.substring(parent.toString().length() + 1);
      return getPathToField(docType, parent.toString()).append(path);
    } catch (NullPointerException e) {
      return new Path(field);
    } catch (NoSuchFieldException ex) {
      logger.error("No such field: " + ex.getMessage());
      return null;
    }
  }

}
