package nl.naturalis.nba.dao.format.calc;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatDate;

import java.util.Date;
import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.ICalculator;

/**
 * Generates a value for the DarwinCore &#34;verbatimEventDate&#34; term.
 * Assumes the entity object is a plain {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimEventDateCalculator implements ICalculator {

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity)
	{
		Specimen specimen = (Specimen) entity.getDocument();
		if (specimen.getGatheringEvent() == null) {
			return EMPTY_STRING;
		}
		Date beginDate = specimen.getGatheringEvent().getDateTimeBegin();
		if (beginDate == null) {
			return EMPTY_STRING;
		}
		Date endDate = specimen.getGatheringEvent().getDateTimeEnd();
		if (endDate == null || beginDate.equals(endDate)) {
			return formatDate(beginDate.toString());
		}
		return formatDate(beginDate) + " | " + formatDate(endDate);
	}

}
