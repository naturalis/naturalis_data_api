package nl.naturalis.nba.dao.es.csv;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

import java.util.Map;

public class ConstantColumn extends AbstractColumn {

	private String value;

	public ConstantColumn(String name, String value)
	{
		super(name);
		this.value = escapeCsv(value);
	}

	@Override
	public String getValue(Map<String, Object> esDocumentAsMap)
	{
		return value;
	}

}
