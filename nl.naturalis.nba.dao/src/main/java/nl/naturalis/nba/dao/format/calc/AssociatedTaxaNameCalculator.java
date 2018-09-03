package nl.naturalis.nba.dao.format.calc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nl.naturalis.nba.api.model.AssociatedTaxon;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * The AssociatedTaxaNameCalculator extracts and concatenates the name and
 * relationType of an AssociatedTaxon from a GatheringEvent.
 * 
 * This calculator can only be used with Specimen documents; henceforth
 * the {@link EntityObject entity object} should be a Specimen object.
 *
 */
public class AssociatedTaxaNameCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException {
    if (docType != Specimen.class) {
      throw new CalculatorInitializationException("AssociatedTaxaNameCalculator can only be used with specimen documents");
    }
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    String associatedTaxa = "";
    Specimen specimen = (Specimen) entity.getDocument();
    
    GatheringEvent gatheringEvent = specimen.getGatheringEvent();
    if (gatheringEvent == null) return associatedTaxa;
    
    List<AssociatedTaxon> taxaList = gatheringEvent.getAssociatedTaxa();
    if (taxaList == null) return associatedTaxa;
    
    Iterator<AssociatedTaxon> iter = taxaList.iterator();
    while (iter.hasNext()) {
      AssociatedTaxon taxon = iter.next();
      associatedTaxa += taxon.getRelationType() + ": " + taxon.getName();
      if (iter.hasNext()) associatedTaxa += " | ";
    }
    return associatedTaxa;
  }

}
