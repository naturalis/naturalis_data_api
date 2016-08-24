package nl.naturalis.nba.dao.es.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import nl.naturalis.nba.dao.es.format.Entity;

class DocumentDateTimeField extends AbstractCsvField {

	private String[] path;

	DocumentDateTimeField(String name, String[] path)
	{
		super(name);
		this.path = path;
	}

	@Override
	public String getValue(Entity entity)
	{
		Object value = readField(entity.getDocument(), path);
		if (value == MISSING_VALUE)
			return EMPTY_STRING;
		return escapeCsv(formatDate(value.toString()));
	}

}
