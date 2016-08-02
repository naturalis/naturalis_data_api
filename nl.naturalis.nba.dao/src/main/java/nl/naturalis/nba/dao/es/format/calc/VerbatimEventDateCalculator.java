package nl.naturalis.nba.dao.es.format.calc;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.util.Date;
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
		Object obj0 = readField(esDocumentAsMap, EVENT_DATE_BEGIN);
		Object obj1 = readField(esDocumentAsMap, EVENT_DATE_END);
		if (obj0 != null && obj0 != MISSING_VALUE) {
			Date date0 = (Date) obj0;
			if (obj1 == null || obj1 == MISSING_VALUE || obj0.equals(obj1)) {
				return escapeCsv(formatDate(date0));
			}
			Date date1 = (Date) obj1;
			String verbatim = formatDate(date0) + " | " + formatDate(date1);
			return escapeCsv(verbatim);
		}
		return null;
	}

}
