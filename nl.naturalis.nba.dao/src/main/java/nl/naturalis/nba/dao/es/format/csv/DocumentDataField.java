package nl.naturalis.nba.dao.es.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;
import static nl.naturalis.nba.dao.es.format.FormatUtil.EMPTY_STRING;
import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.net.URI;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.dao.es.format.AbstractField;
import nl.naturalis.nba.dao.es.format.EntityObject;

class DocumentDataField extends AbstractField {

	private Path path;

	DocumentDataField(String name, URI term, Path path)
	{
		super(name, term);
		this.path = path;
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = readField(entity.getDocument(), path);
		if (value == MISSING_VALUE) {
			return EMPTY_STRING;
		}
		return escapeCsv(value.toString());
	}

}
