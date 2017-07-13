package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

class EntityDataField extends AbstractField {

	private Path path;

	EntityDataField(String name, URI term, Path path)
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
		
		// HACK: if StringEscapeUtils.escapeCsv correctly implements CSV escaping,
		// this should not be necessary. However, GBIF doesn't like it
		String s = value.toString().replace('\n', ' ');
		s = value.toString().replace('\r', ' ');
		
		return escapeCsv(s);
	}

}
