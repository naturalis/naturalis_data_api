package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IField;

/**
 * An implementation if {@link IField} that retrieves its value from the nested
 * object that functions as the {@link EntityObject}. See also
 * {@link DocumentDataField}.
 * 
 * @author Ayco Holleman
 *
 */
class EntityDataField extends AbstractField {

	private PathValueReader pvr;

	EntityDataField(String name, URI term, Path path)
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
		return value.toString();
	}

}
