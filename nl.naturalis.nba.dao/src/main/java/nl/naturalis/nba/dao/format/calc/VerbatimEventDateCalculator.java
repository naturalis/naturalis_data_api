package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatISO8601ShortDate;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * A calculator for the verbatimEventDate field in a DarwinCore archive for
 * specimens. Assumes the {@link EntityObject entity object} is a plain
 * {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimEventDateCalculator implements ICalculator {

  @Override
  public void initialize(Class<? extends IDocumentObject> docType, Map<String, String> args) throws CalculatorInitializationException
  {
  }

	@Override
	public String calculateValue(EntityObject entity)
	{
		Specimen specimen = (Specimen) entity.getDocument();
		if (specimen.getGatheringEvent() == null) {
			return EMPTY_STRING;
		}
		OffsetDateTime beginDate = specimen.getGatheringEvent().getDateTimeBegin();
		if (beginDate == null) {
			return EMPTY_STRING;
		}
		OffsetDateTime endDate = specimen.getGatheringEvent().getDateTimeEnd();
		if (endDate == null || isSameDay(beginDate, endDate)) {
			return formatISO8601ShortDate(beginDate);
		}
		return formatISO8601ShortDate(beginDate) + " / " + formatISO8601ShortDate(endDate);
	}
	
	private static boolean isSameDay(OffsetDateTime begin, OffsetDateTime end)
	{
	  if (begin == null || end == null) return false;
	  ZonedDateTime b = begin.atZoneSameInstant(ZoneId.of("Europe/Paris"));
	  ZonedDateTime e = end.atZoneSameInstant(ZoneId.of("Europe/Paris"));
	  if (b.getYear() == e.getYear() && 
	      b.getMonth() == e.getMonth() &&
	      b.getDayOfMonth() == e.getDayOfMonth()) {
	    return true;
	    }
	  return false;
	}

}
