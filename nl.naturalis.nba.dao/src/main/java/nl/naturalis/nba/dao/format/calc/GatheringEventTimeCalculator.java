package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import java.time.LocalTime;
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
 * dateTimeBegin and dateTimeEnd from the document GatheringEvent. The 
 * calculator returns the time (HH:mm) or begin and endtime according
 * to the following table:
 *
 * | dateTimeBegin       | dateTimeEnd         | eventTime   |
 * |---------------------------------------------------------|
 * | 2018-04-01T00:00:00 | 2018-04-01T00:00:00 | 00:00       |
 * | 2018-04-01T00:00:00 | 2018-04-01T16:00:00 | 00:00/16:00 |
 * | 2018-04-01T00:00:00 | 2018-04-01T23:59:59 |             |
 * | 2018-04-01T14:30:00 | 2018-04-01T14:30:00 | 14:30       |
 * | 2018-04-01T14:30:00 | 2018-04-01T15:30:00 | 14:30/15:30 |
 * | 2018-04-01T14:30:00 | 2018-04-01T23:59:59 | 14:30/23:59 |
 * |                     |                     |             |
 * | 2018-04-01T00:00:00 | 2018-04-02T00:00:00 | 00:00       |
 * | 2018-04-01T00:00:00 | 2018-04-02T16:00:00 | 00:00/16:00 |
 * | 2018-04-01T00:00:00 | 2018-04-02T23:59:59 |             | 
 * | 2018-04-01T14:30:00 | 2018-04-02T14:30:00 | 14:30       |
 * | 2018-04-01T14:30:00 | 2018-04-02T15:30:00 | 14:30/15:30 |
 * | 2018-04-01T14:30:00 | 2018-04-02T23:59:59 | 14:30/23:59 |
 * |                     |                     |             |
 * | 2018-04-01T00:00:00 | 2018-05-01T00:00:00 | 00:00       |
 * | 2018-04-01T00:00:00 | 2018-05-01T16:00:00 | 00:00/16:00 |
 * | 2018-04-01T00:00:00 | 2018-05-01T23:59:59 |             |
 * | 2018-04-01T14:30:00 | 2018-05-01T14:30:00 | 14:30       |
 * | 2018-04-01T14:30:00 | 2018-05-01T15:30:00 | 14:30/15:30 |
 * | 2018-04-01T14:30:00 | 2018-05-01T23:59:59 | 14:30/23:59 |
 * |                     |                     |             | 
 * | 2018-04-01T00:00:00 | null                | 00:00       |
 * | 2018-04-01T10:15:00 | null                | 10:15       |
 * | null                | 2018-04-01T00:00:00 | /00:00      |
 * | null                | 2018-04-01T16:00:00 | /16:00      |
 * 
 * 
 * NOTE: some source systems do not record begin- and endtime of a 
 * gathering event. Since the document store will always store a time
 * (even if none is provided), it has been decided that when source 
 * systems provide a begin time = 00:00:00 and end time = 23:59:59 
 * this should be interpreted as a gatheringEvent having no event time.   
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
    DateTimeFormatter toTimeFormat = DateTimeFormatter.ofPattern("HH:mm");
    OffsetDateTime dateTimeBegin;
    OffsetDateTime dateTimeEnd;
    LocalTime startOfDay = LocalTime.parse("00:00", DateTimeFormatter.ISO_TIME);
    LocalTime endOfDay =   LocalTime.parse("23:59", DateTimeFormatter.ISO_TIME);
    
    if (specimen.getGatheringEvent() == null) {
      return EMPTY_STRING;
    }
    if (specimen.getGatheringEvent().getDateTimeBegin() == null && 
        specimen.getGatheringEvent().getDateTimeEnd() == null) 
    {
      return EMPTY_STRING;
    }
    if (specimen.getGatheringEvent().getDateTimeBegin() != null && 
        specimen.getGatheringEvent().getDateTimeEnd() == null) 
    {
      dateTimeBegin = specimen.getGatheringEvent().getDateTimeBegin();
      return dateTimeBegin.format(toTimeFormat).toString();
    }
    if (specimen.getGatheringEvent().getDateTimeBegin() == null && 
        specimen.getGatheringEvent().getDateTimeEnd() != null) 
    {
      dateTimeEnd = specimen.getGatheringEvent().getDateTimeEnd();
      return "/" + dateTimeEnd.format(toTimeFormat).toString();
    }
    
    dateTimeBegin = specimen.getGatheringEvent().getDateTimeBegin();
    dateTimeEnd = specimen.getGatheringEvent().getDateTimeEnd();
    if (dateTimeBegin.toLocalTime().format(toTimeFormat).equals(startOfDay.format(toTimeFormat)) && 
        dateTimeEnd.toLocalTime().format(toTimeFormat).equals(endOfDay.format(toTimeFormat))) 
    {
      return EMPTY_STRING;
    }
    if (dateTimeBegin.toLocalTime().format(toTimeFormat).equals(dateTimeEnd.toLocalTime().format(toTimeFormat))) 
    {
      return dateTimeBegin.format(toTimeFormat).toString();
    }        
    return dateTimeBegin.format(toTimeFormat) + "/" + dateTimeEnd.format(toTimeFormat);
  }

}
