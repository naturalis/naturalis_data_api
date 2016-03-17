package nl.naturalis.nba.elasticsearch.map;

import java.util.LinkedHashMap;

class ESSimpleField extends ESScalar {

	private LinkedHashMap<String, ESScalar> fields;

	ESSimpleField(Type type)
	{
		super(type);
	}

	LinkedHashMap<String, ESScalar> getFields()
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
			fields = new LinkedHashMap<>(4);
		}
		fields.put(name, field);
	}

}
