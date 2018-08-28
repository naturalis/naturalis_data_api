package nl.naturalis.nba.dao.format.calc;

import static java.lang.String.format;
import java.util.Map;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.common.InvalidPathException;
import nl.naturalis.nba.common.PathUtil;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator that converts the value from a DateTime field to a calendar 
 * data (YYYY-MM-DD).
 *
 */
public class CalendarDateCalculator implements ICalculator {

  private PathValueReader pathValueReader;
  
  @Override
  public void initialize(Map<String, String> args) throws CalculatorInitializationException {
  }

  @Override
  public void initialize(Class<? extends IDocumentObject> cls, Map<String, String> args) throws CalculatorInitializationException {
    String dateField = args.get(null);
    
    System.out.println("ARGS: " + args.get(null));
    
    Path path = new Path(dateField);
    pathValueReader = new PathValueReader(dateField);
    try {
      PathUtil.validate(path, MappingFactory.getMapping(cls));
    } catch (InvalidPathException e) {
      String msg = format("Entity %s: %s", dateField, e.getMessage());
      throw new CalculatorInitializationException(msg);
    }    
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    
    try {
      Object obj = pathValueReader.read(entity.getDocument());
      if (obj != null) {
        System.out.println(obj.toString());
        return obj.toString();
      }
    } catch (Exception e) {
      System.out.println("Error in CalendarDateCalculator: " + e.getMessage());
      // throw new InvalidPathException(e.getMessage());
    }
    return "";
    }


}
