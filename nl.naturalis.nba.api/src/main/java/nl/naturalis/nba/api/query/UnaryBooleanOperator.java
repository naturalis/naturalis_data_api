package nl.naturalis.nba.api.query;

/**
 * Defines a single constant representing the unary boolean operator NOT. This
 * constant that can be used in a {@link Condition query condition} to indicate
 * that the condition is to be negated.
 * 
 * @author Ayco Holleman
 *
 */
public enum UnaryBooleanOperator
{
	NOT;

	public static UnaryBooleanOperator parse(String s)
	{
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (s.equalsIgnoreCase("NOT") || s.equals("!")) {
			return NOT;
		}
		throw new IllegalArgumentException("No such unary boolean operator: " + s);
	}
}
