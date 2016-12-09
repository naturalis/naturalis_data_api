package nl.naturalis.nba.dao.format.csv;

import java.net.URI;

import org.apache.commons.lang3.StringEscapeUtils;

import nl.naturalis.nba.common.Path;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.format.EntityObject;
import nl.naturalis.nba.dao.format.FieldWriteException;
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
		Object value = path.read(entity.getData());
		if (value == JsonUtil.MISSING_VALUE) {
			return "";
		}
		return StringEscapeUtils.escapeCsv(value.toString());
	}

}
