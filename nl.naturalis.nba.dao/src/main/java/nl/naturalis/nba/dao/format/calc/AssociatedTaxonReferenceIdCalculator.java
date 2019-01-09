package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * AssociatedTaxonReferenceIdCalculator is a calculator that returns the 
 * id of the Taxon document to which the current MultiMediaObject belongs.
 *
 * This Calculator can be used with MultiMediaObject documents only! The 
 * {@link EntityObject entity object} should therefore be a MultiMediaObject.
 */
public class AssociatedTaxonReferenceIdCalculator implements ICalculator {
  
  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException 
  {
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    MultiMediaObject multiMediaObject = (MultiMediaObject) entity.getDocument();
    String id = multiMediaObject.getAssociatedTaxonReference();
    String suffix = "@" + multiMediaObject.getSourceSystem().getCode();
    if (id == null)
      return EMPTY_STRING;
    if (id.endsWith(suffix)) 
      return id.substring(0, id.length()-suffix.length());
    return id;
  }

}
