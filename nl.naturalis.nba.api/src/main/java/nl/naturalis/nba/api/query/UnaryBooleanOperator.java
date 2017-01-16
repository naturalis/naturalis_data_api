package nl.naturalis.nba.api.query;

/**
 * Defines a single constant representing the unary boolean operator NOT. This
 * constant can be used to negate a {@link QueryCondition query condition}. See
 * {@link QueryCondition#setNot(UnaryBooleanOperator) Condition.setNot}.
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
