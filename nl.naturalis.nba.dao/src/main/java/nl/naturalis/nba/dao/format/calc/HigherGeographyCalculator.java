package nl.naturalis.nba.dao.format.calc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nl.naturalis.nba.api.model.AreaClass;
import nl.naturalis.nba.api.model.GatheringEvent;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.NamedArea;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * gatheringEvent.areaClass = 'higherGeography'
 *
 */
public class HigherGeographyCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args)
      throws CalculatorInitializationException {
    if (docType != Specimen.class) {
      throw new CalculatorInitializationException("HigherGeographyCalculator can only be used with specimen documents");
    }
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException {
    String areaName = "";
    Specimen specimen = (Specimen) entity.getDocument();

    GatheringEvent gatheringEvent = specimen.getGatheringEvent();
    if (gatheringEvent == null) return "";
    
    List<NamedArea> areas = null;
    if (gatheringEvent.getNamedAreas() != null) {
      areas = gatheringEvent.getNamedAreas();
    } else {
      return "";
    } 
    
    Iterator<NamedArea> namedAreas = areas.iterator();
    while (namedAreas.hasNext()) {
      NamedArea area = namedAreas.next();
      if (area.getAreaClass() == null || area.getAreaName() == null) continue;
      if (area.getAreaClass().equals(AreaClass.HIGHERGEOGRAPHY.name())) {
        if (areaName.length() > 0) areaName += " | ";
        areaName += area.getAreaName();        
      }
    }
    return areaName;
  }

}
