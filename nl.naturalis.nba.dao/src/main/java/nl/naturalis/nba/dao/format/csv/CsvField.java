package nl.naturalis.nba.dao.format.csv;

import static nl.naturalis.nba.common.json.JsonUtil.MISSING_VALUE;
import static nl.naturalis.nba.common.json.JsonUtil.readField;

import java.net.URI;

import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FieldWriteException;
import nl.naturalis.nba.dao.format.FormatUtil;
import nl.naturalis.nba.dao.format.IField;

class CsvField implements IField {

	private Path path;
	private String name;

	CsvField(String path)
	{
		this.path = new Path(path);
		this.name = this.path.getElement(this.path.countElements() - 1);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public URI getTerm()
	{
		return null;
	}

	@Override
	public String getValue(EntityObject entity) throws FieldWriteException
	{
		Object value = readField(entity.getData(), path);
		if (value == MISSING_VALUE) {
			return FormatUtil.EMPTY_STRING;
		}
		return value.toString();
	}

}
