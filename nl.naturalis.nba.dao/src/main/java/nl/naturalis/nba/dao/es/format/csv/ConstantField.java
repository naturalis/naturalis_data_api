package nl.naturalis.nba.dao.es.format.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import nl.naturalis.nba.dao.es.format.Entity;

class ConstantField extends AbstractCsvField {

	private String value;

	ConstantField(String name, String value)
	{
		super(name);
		this.value = escapeCsv(value);
	}

	@Override
	public String getValue(Entity esDocumentAsMap)
	{
		return value;
	}

}
