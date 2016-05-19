package nl.naturalis.nba.api.query;

/**
 * Symbolic constant used in a {@link Condition query condition} indicating that
 * the condition is to be negated.
 * 
 * @author Ayco Holleman
 *
 */
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
