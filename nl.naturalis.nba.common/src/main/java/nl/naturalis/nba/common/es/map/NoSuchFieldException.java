package nl.naturalis.nba.common.es.map;

public class NoSuchFieldException extends Exception {

	private final String field;

	public NoSuchFieldException(String field)
	{
		super("No such field: \"" + field + "\"");
		this.field = field;
	}

	public String getField()
	{
		return field;
	}

}
