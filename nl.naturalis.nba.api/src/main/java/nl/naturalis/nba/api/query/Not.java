package nl.naturalis.nba.api.query;

public enum Not
{
	NOT;

	public static Not parse(String value)
	{
		if (value == null || value.isEmpty()) {
			return null;
		}
		if (value.equalsIgnoreCase("NOT")) {
			return NOT;
		}
		throw new IllegalArgumentException(value);
	}
}
