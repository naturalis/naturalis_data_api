package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatDate;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

class EntityDateTimeField extends AbstractField {

	private PathValueReader pvr;

	EntityDateTimeField(String name, URI term, Path path)
	{
		super(name, term);
		this.pvr = new PathValueReader(path);
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = pvr.read(entity.getEntity());
		if (value == null) {
			return EMPTY_STRING;
		}
		return formatDate(value.toString());
	}

}
