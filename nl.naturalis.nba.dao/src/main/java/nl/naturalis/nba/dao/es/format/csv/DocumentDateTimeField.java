package nl.naturalis.nba.dao.es.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.es.format.FormatUtil.formatDate;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.EntityObject;

class DocumentDateTimeField extends AbstractCsvField {

	private final String[] path;

	DocumentDateTimeField(String name, Path path)
	{
		super(name);
		this.path = path.getElements();
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = readField(entity.getDocument(), path);
		if (value == MISSING_VALUE) {
			return EMPTY_STRING;
		}
		return escapeCsv(formatDate(value.toString()));
	}

}