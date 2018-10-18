package nl.naturalis.nba.dao.format.calc;

import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * OccurrenceIdCalculator is a calculator that adds the string given as
 * argument as a prefix to the sourceSystemId and returns this as a new
 * string.
 * 
 * Example: when the argument is "https://observation.org/observation/"
 * and the sourceSystemId is "98489255", the calculator will return:
 * https://observation.org/observation/98489255
 * 
 * Note: when no argument is given, or the argument is left blank, the
 * calculator will return only the sourceSystemId.
 *
 */
public class OccurrenceIdCalculator implements ICalculator {

  private String url = "";
  
  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException {
    if (args.get(null) != null) url = args.get(null).trim();
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    Specimen specimen = (Specimen) entity.getDocument();
    String sourceSystemId = specimen.getSourceSystemId();
    return url + sourceSystemId;
  }

}
