package nl.naturalis.nba.dao.es.csv;

import java.util.Map;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;
import nl.naturalis.nba.common.json.JsonUtil;

public class DataColumn extends AbstractColumn {

	private String[] path;

	public DataColumn(String name, String[] path)
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
