package nl.naturalis.nba.elasticsearch.map;

import java.util.HashMap;

public enum ESDataType
{

	STRING("string"),
	INTEGER("integer"),
	BOOLEAN("boolean"),
	DATE("date"),
	BYTE("byte"),
	SHORT("short"),
	LONG("long"),
	FLOAT("float"),
	DOUBLE("double");

	private static final HashMap<String, ESDataType> reverse;

	static {
		reverse = new HashMap<String, ESDataType>(10, 1);
		for (ESDataType t : values()) {
			reverse.put(t.esName, t);
		}
	}

	public static ESDataType parse(String name)
	{
		return reverse.get(name);
	}

	private final String esName;

	private ESDataType(String esName)
	{
		this.esName = esName;
	}

	public String toString()
	{
		return esName;
	}
}