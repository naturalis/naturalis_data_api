package nl.naturalis.nba.elasticsearch.map;

import java.util.HashMap;

public enum Type
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

	private static final HashMap<String, Type> reverse;

	static {
		reverse = new HashMap<String, Type>(10, 1);
		for (Type t : values()) {
			reverse.put(t.esName, t);
		}
	}

	public static Type parse(String name)
	{
		return reverse.get(name);
	}

	private final String esName;

	private Type(String esName)
	{
		this.esName = esName;
	}

	public String toString()
	{
		return esName;
	}
}
