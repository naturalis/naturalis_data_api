package nl.naturalis.nba.elasticsearch.map;

import java.util.LinkedHashMap;

class ESMainField extends ESScalar {

	private LinkedHashMap<String, ESScalar> fields;

	ESMainField(Type type)
	{
		super(type);
	}

	ESMainField()
	{
		super();
	}

	LinkedHashMap<String, ESScalar> getFields()
	{
		return fields;
	}

	public void addScalar(String name, ESScalar scalar)
	{
		if (fields == null) {
			fields = new LinkedHashMap<>(4);
		}
		fields.put(name, scalar);
	}

}
