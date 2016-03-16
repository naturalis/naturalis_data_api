package nl.naturalis.nba.elasticsearch.map;

import java.util.LinkedHashMap;

class ESObject extends ESField {

	private final LinkedHashMap<String, ESField> properties;

	ESObject()
	{
		properties = new LinkedHashMap<>();
	}

	void addField(String name, ESField f)
	{
		properties.put(name, f);
	}

	LinkedHashMap<String, ESField> getProperties()
	{
		return properties;
	}

}
