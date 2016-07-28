package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.util.Map;

public class VerbatimEventDateCalculator implements ICalculator {

	private static final String[] EVENT_DATE_BEGIN;
	private static final String[] EVENT_DATE_END;

	static {
		EVENT_DATE_BEGIN = "gatheringEvent.dateTimeBegin".split("\\.");
		EVENT_DATE_END = "gatheringEvent.dateTimeEnd".split("\\.");
	}

	@Override
	public Object calculateValue(Map<String, Object> esDocumentAsMap)
	{
		Object date0 = readField(esDocumentAsMap, EVENT_DATE_BEGIN);
		Object date1 = readField(esDocumentAsMap, EVENT_DATE_END);
		if (date0 != null && date0 != MISSING_VALUE) {
			if (date1 == null || date1 == MISSING_VALUE || date0.equals(date1)) {
				return date0;
			}
			return date0 + " | " + date1;
		}
		return null;
	}

}
