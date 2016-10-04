package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;

import java.util.Map;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.CalculatorInitializationException;
import nl.naturalis.nba.dao.es.format.EntityObject;
import nl.naturalis.nba.dao.es.format.ICalculator;

/**
 * Generates a value for the DarwinCore &#34;verbatimEventDate&#34; term.
 * Assumes the entity object is a plain {@link Specimen} document.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimEventDateCalculator implements ICalculator {

	private static final Path beginDatePath = new Path("gatheringEvent.dateTimeBegin");
	private static final Path endDatePath = new Path("gatheringEvent.dateTimeEnd");

	@Override
	public void initialize(Map<String, String> args) throws CalculatorInitializationException
	{
	}

	@Override
	public Object calculateValue(EntityObject entity)
	{
		Object beginDate = beginDatePath.read(entity.getData());
		if (beginDate == MISSING_VALUE) {
			return EMPTY_STRING;
		}
		Object endDate = endDatePath.read(entity.getData());
		if (endDate == MISSING_VALUE || beginDate.equals(endDate)) {
			return formatDate(beginDate.toString());
		}
		return formatDate(beginDate.toString()) + " | " + formatDate(endDate.toString());
	}

}
