package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;
import static nl.naturalis.nba.dao.format.FormatUtil.formatDate;

import java.net.URI;
import java.time.OffsetDateTime;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

/**
 * A {@code EntityDateField} writes a date string to a CSV field, which it reads
 * from a date field within a nested object within an Elasticsearch document
 * (the so-called {@link EntityObject}).
 * 
 * @author Ayco Holleman
 *
 */
class EntityDateField extends AbstractField {

	private PathValueReader pvr;

	EntityDateField(String name, URI term, Path path)
	{
		super(name, term);
		this.pvr = new PathValueReader(path);
	}

	@Override
	public String getValue(EntityObject entity)
	{
		OffsetDateTime value = (OffsetDateTime) pvr.read(entity.getEntity());
		return value == null ? EMPTY_STRING : formatDate(value);
	}

}
