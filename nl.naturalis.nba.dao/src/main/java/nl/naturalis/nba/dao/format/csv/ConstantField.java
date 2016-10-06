package nl.naturalis.nba.dao.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.net.URI;

import nl.naturalis.nba.dao.format.AbstractField;
import nl.naturalis.nba.dao.format.EntityObject;

class ConstantField extends AbstractField {

	private String value;

	ConstantField(String name, URI term, String value)
	{
		super(name, term);
		this.value = escapeCsv(value);
	}

	@Override
	public String getValue(EntityObject entity)
	{
		return value;
	}

}