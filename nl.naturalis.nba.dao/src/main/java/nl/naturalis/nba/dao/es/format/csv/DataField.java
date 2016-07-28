package nl.naturalis.nba.dao.es.format.csv;

import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import nl.naturalis.nba.common.json.JsonUtil;

class DataField extends AbstractCsvField {

	private String[] path;

	DataField(String name, String[] path)
	{
		super(name);
		this.path = path;
	}

	@Override
	public String getValue(Map<String, Object> esDocumentAsMap)
	{
		Object value = JsonUtil.readField(esDocumentAsMap, path);
		if (value == null || value == JsonUtil.MISSING_VALUE)
			return "";
		return escapeCsv(value.toString());
	}

}
