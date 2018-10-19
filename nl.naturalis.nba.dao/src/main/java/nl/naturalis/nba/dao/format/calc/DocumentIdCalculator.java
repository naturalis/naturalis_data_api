package nl.naturalis.nba.dao.format.calc;

import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * DocumentIdCalculator is a calculator that returns the Elasticsearch id of a
 * document. This calculator can be used for all document types.
 *
 */
public class DocumentIdCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException 
  {}

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    IDocumentObject doc = (IDocumentObject) entity.getDocument();
    return doc.getId();
  }

}
