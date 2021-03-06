package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.dao.format.FormatUtil.EMPTY_STRING;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.api.model.VernacularName;
import nl.naturalis.nba.common.PathValueReader;
import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.IField;

/**
 * An implementation of {@link IField} which retrieves its value from the parent
 * document rather than from the nested object that is the prime focus of the
 * CSV file being written. See {@link EntityObject}. For example, if you wanted
 * to create a CSV file with vernacular names, the {@link VernacularName} object
 * within the {@link Taxon} document would be the entity object: several CSV
 * records might be written from the same {@code Taxon} document, because a
 * {@code Taxon} document may contain multiple {@code VernacularName} objects.
 * However, you would probably still also want to include the taxon ID in the
 * CSV record. In that case you would need a {@code DocumentField}, because
 * the taxon ID is not part of the {@code VernacularName} object. It sits at the
 * top-most level of the {@code Taxon} document. See also {@link EntityField}.
 */
class DocumentField extends AbstractField {

	private PathValueReader pvr;

	DocumentField(String name, URI term, Boolean isCoreId, Path path)
	{
		super(name, term, isCoreId);
		this.pvr = new PathValueReader(path);
	}

	@Override
	public String getValue(EntityObject entity)
	{
		Object value = pvr.read(entity.getDocument());
		return value == null ? EMPTY_STRING : value.toString();
	}

}
