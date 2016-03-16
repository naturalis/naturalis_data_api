package nl.naturalis.nba.elasticsearch.schema;

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
