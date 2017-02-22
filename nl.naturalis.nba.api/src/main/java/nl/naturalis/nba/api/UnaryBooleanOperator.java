package nl.naturalis.nba.api;

import com.fasterxml.jackson.annotation.JsonCreator;

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

	/**
	 * Parses the specified string into the NOT operator. If you pass
	 * {@code null} or an empty string, {@code null} is returned. If you pass
	 * the string "NOT" (case insensitive) or "!", {@code NOT} is returned. Else
	 * an {@link IllegalArgumentException} is thrown.
	 * 
	 * @param s
	 * @return
	 */
	@JsonCreator
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
