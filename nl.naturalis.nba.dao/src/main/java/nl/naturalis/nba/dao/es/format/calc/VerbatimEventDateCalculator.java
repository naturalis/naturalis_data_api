package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;

import nl.naturalis.nba.dao.es.format.EntityObject;

/**
 * Generates a value for the &#46;verbatimEventDate&#46; field.
 * 
 * @author Ayco Holleman
 *
 */
public class VerbatimEventDateCalculator implements ICalculator {

	private static final String[] EVENT_DATE_BEGIN;
	private static final String[] EVENT_DATE_END;

	static {
		EVENT_DATE_BEGIN = "gatheringEvent.dateTimeBegin".split("\\.");
		EVENT_DATE_END = "gatheringEvent.dateTimeEnd".split("\\.");
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
