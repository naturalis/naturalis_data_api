package nl.naturalis.nba.elasticsearch.map;

import java.util.LinkedHashMap;

public class ESSimpleField extends ESScalar {

	private LinkedHashMap<String, ESScalar> fields;

	public ESSimpleField(ESDataType eSDataType)
	{
		super(eSDataType);
	}

	public LinkedHashMap<String, ESScalar> getFields()
	{
		return fields;
	}

	public void addRawField()
	{
		addToFields("raw", ESScalar.RAW);
	}

	public void addToFields(String name, ESScalar field)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(2);
		}
		fields.put(name, field);
	}

}
