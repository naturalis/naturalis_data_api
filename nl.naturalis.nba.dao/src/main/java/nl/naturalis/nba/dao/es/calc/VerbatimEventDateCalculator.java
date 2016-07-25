package nl.naturalis.nba.dao.es.calc;

import java.util.Map;

import nl.naturalis.nba.common.json.JsonUtil;

public class VerbatimEventDateCalculator implements ICalculator {

	@Override
	public Object calculateValue(Map<String, Object> esDocumentAsMap)
	{
		Object date0 = JsonUtil.readField(esDocumentAsMap, "gatheringEvent.dateTimeBegin");
		Object date1 = JsonUtil.readField(esDocumentAsMap, "gatheringEvent.dateTimeEnd");
		if (date0 != null) {
			if (date1 == null || date0.equals(date1)) {
				return date0;
			}
			return date0 + " | " + date1;
		}
		return null;
	}

}
