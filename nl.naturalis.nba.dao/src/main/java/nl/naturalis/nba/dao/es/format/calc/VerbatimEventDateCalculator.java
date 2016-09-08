package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;

import java.util.Map;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.EntityObject;

/**
 * Generates a value for the &#46;verbatimEventDate&#46; field.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimEventDateCalculator implements ICalculator {

	private static final Path EVENT_DATE_BEGIN = new Path("gatheringEvent.dateTimeBegin");
	private static final Path EVENT_DATE_END = new Path("gatheringEvent.dateTimeEnd");

	@Override
	public void initialize(Map<String, String> args)
	{
	}

	@Override
	public Object calculateValue(EntityObject entity)
	{
		Object obj0 = readField(entity.getData(), EVENT_DATE_BEGIN);
		if (obj0 == MISSING_VALUE)
			return EMPTY_STRING;
		Object obj1 = readField(entity.getData(), EVENT_DATE_END);
		if (obj1 == MISSING_VALUE || obj0.equals(obj1))
			return formatDate(obj0.toString());
		return formatDate(obj0.toString()) + " | " + formatDate(obj1.toString());
	}

}
