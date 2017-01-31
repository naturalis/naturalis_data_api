package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatDate;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

class EntityDateTimeField extends AbstractField {

	private Path path;

	EntityDateTimeField(String name, URI term, Path path)
	{
		super(name, term);
		this.path = path;
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = readField(entity.getData(), path);
		if (value == MISSING_VALUE) {
			return EMPTY_STRING;
		}
		return escapeCsv(formatDate(value.toString()));
	}

}
