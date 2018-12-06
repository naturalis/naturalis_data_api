package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IField;

/**
 * An implementation of {@link IField} that retrieves its value from the nested
 * object that functions as the {@link EntityObject}. See also
 * {@link DocumentField}.
 * 
 * @author Ayco Holleman
 *
 */
class EntityField extends AbstractField {

	private PathValueReader pvr;

	EntityField(String name, URI term, Boolean isCoreId, Path path)
	{
		super(name, term, isCoreId);
		this.pvr = new PathValueReader(path);
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = pvr.read(entity.getEntity());
		return value == null ? EMPTY_STRING : value.toString();
	}

}
