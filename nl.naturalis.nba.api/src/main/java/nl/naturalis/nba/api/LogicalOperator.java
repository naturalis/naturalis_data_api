package nl.naturalis.nba.api;

/**
 * Symbolic constants for the two logical operators (AND and OR). Logical
 * operators are used to combine the {@link QueryCondition query conditions} within a
 * {@link QuerySpec query specification}.
 * 
 * @author Ayco Holleman
 *
 */
public enum LogicalOperator
{
	AND, OR;

	public static LogicalOperator parse(String s)
	{
		if (s == null || s.isEmpty()) {
			return null;
		}
		if (s.equalsIgnoreCase("AND") || s.equals("&&")) {
			return AND;
		}
		if (s.equalsIgnoreCase("OR") || s.equals("||")) {
			return OR;
		}
		throw new IllegalArgumentException("No such logical operator: " + s);
	}

}
