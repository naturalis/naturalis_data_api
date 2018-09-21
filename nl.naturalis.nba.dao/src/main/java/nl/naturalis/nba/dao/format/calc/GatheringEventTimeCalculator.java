package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculationException;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * GatheringEventTimeCalculator is a calculator that compares the fields
 * dateTimeBegin and dateTimeEnd from the document GatheringEvent.
 * The calculator return the time (HH:mm) when both fields are equal;
 * in all other cases an empty String will be returned.
 * 
 * Raison d'être: some source systems do not record begin- and endtime of an
 * event, but only the time of the gathering event. When no time is provided 
 * (i.e. only a date), the document store will normally record this event 
 * with a begin time = 00:00:00, and end time = 23:59:59. When however a 
 * gathering time is available, this is stored as both begin- and endtime 
 * in the document store (with begin- and enddate having equal dates). This 
 * calcular provides precisely those gathering times.   
 *
 */
public class GatheringEventTimeCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args)
      throws CalculatorInitializationException 
  {
  }

  @Override
  public Object calculateValue(EntityObject entity) throws CalculationException 
  {
    Specimen specimen = (Specimen) entity.getDocument();
    if (specimen.getGatheringEvent() == null) {
      return EMPTY_STRING;
    }    
    if (specimen.getGatheringEvent().getDateTimeBegin() == null || specimen.getGatheringEvent().getDateTimeEnd() == null) {
      return EMPTY_STRING;
    }
    OffsetDateTime dateTimeBegin = specimen.getGatheringEvent().getDateTimeBegin();
    OffsetDateTime dateTimeEnd = specimen.getGatheringEvent().getDateTimeEnd();
    if (dateTimeBegin.equals(dateTimeEnd)) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
      return dateTimeBegin.format(formatter).toString();
    }
    return EMPTY_STRING;
  }

}
