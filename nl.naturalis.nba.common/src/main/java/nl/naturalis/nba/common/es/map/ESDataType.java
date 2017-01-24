package nl.naturalis.nba.common.es.map;

import java.util.HashMap;

public enum ESDataType
{

	KEYWORD,
	TEXT,
	INTEGER,
	BOOLEAN,
	DATE,
	BYTE,
	SHORT,
	LONG,
	FLOAT,
	DOUBLE,
	GEO_POINT,
	GEO_SHAPE,
	OBJECT,
	NESTED;

	/*
	 * Map data type names to enum constants.
	 */
	private static final HashMap<String, ESDataType> reverse;

	static {
		reverse = new HashMap<String, ESDataType>(16, 1);
		for (ESDataType t : values()) {
			reverse.put(t.esName, t);
		}
	}

	public static ESDataType parse(String name)
	{
		return reverse.get(name);
	}

	private final String esName;

	private ESDataType()
	{
		this.esName = name().toLowerCase();
	}

	private ESDataType(String esName)
	{
		this.esName = esName;
	}

	@Override
	public String toString()
	{
		return esName;
	}
}
