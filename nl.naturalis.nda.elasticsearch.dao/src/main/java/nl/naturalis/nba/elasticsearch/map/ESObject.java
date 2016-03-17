package nl.naturalis.nba.elasticsearch.map;

import java.util.LinkedHashMap;

public class ESObject extends ESField {

	private final LinkedHashMap<String, ESField> properties;

	public ESObject()
	{
		properties = new LinkedHashMap<>();
	}

	public LinkedHashMap<String, ESField> getProperties()
	{
		return properties;
	}

	public void addField(String name, ESField f)
	{
		properties.put(name, f);
	}

}
