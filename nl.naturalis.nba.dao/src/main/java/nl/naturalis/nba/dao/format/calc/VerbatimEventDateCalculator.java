package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatDate;

import java.time.OffsetDateTime;
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
	public Object calculateValue(EntityObject entity)
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
		if (endDate == null || beginDate.equals(endDate)) {
			return formatDate(beginDate);
		}
		return formatDate(beginDate) + " | " + formatDate(endDate);
	}

}
