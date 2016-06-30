package nl.naturalis.nba.api.query;

import java.util.Collection;
import java.util.HashSet;

/**
 * Provides symbolic constants for the operators that can be used in a
 * {@link Condition query condition}.
 * 
 * @author Ayco Holleman
 *
 */
public enum ComparisonOperator
{

	/**
	 * Operator used to establish that search term and field value are equal.
	 * For alpha-numeric fields the search term and field value are compared in
	 * a <i>case sensitive</i> way. The EQUALS operator also allows you to
	 * search for null/empty values, simply by leaving the
	 * {@link Condition#getValue() value} property of a query {@link Condition
	 * condition} {@code null}.
	 */
	EQUALS("="),
	/**
	 * Operator used to establish that search term and field value are not
	 * equal. For alpha-numeric fields the search term and field value are
	 * compared in a <i>case sensitive</i> way. The search term may be null when
	 * using the NOT_EQUALS operator. This is equivalent to using the IS NOT NULL
	 * operator in SQL.
	 */
	NOT_EQUALS("!="),
	/**
	 * Operator used to establish that search term and field value are equal
	 * ignoring case. Can only be used for alpha-numeric fields. The search term
	 * may be null when using the EQUALS operator. This is equivalent to using
	 * the IS NULL operator in SQL.
	 */
	EQUALS_IC,
	/**
	 * Operator used to establish that search term and field value are not
	 * equal, even when ignoring case. Can only be used for alpha-numeric
	 * fields. The search term may be null when using the EQUALS operator. This
	 * is equivalent to using the IS NOT NULL operator in SQL.
	 */
	NOT_EQUALS_IC,
	/**
	 * Operator used to establish that the search term is a substring of the
	 * field value. Can only be used for alpha-numeric fields. Search term and
	 * field value are compared in a case insensitive way. The search term must
	 * not be null.
	 */
	LIKE,
	/**
	 * Operator used to establish that the search term is not a substring of the
	 * field value. Can only be used for alpha-numeric fields. Search term and
	 * field value are compared in a case insensitive way. The search term must
	 * not be null.
	 */
	NOT_LIKE,
	/**
	 * LESS THAN. Can only be used for number fields and date fields.
	 */
	LT("<"),
	/**
	 * LESS THAN OR EQUAL. Can only be used for number fields and date fields.
	 */
	LTE("<="),
	/**
	 * GREATER THAN. Can only be used for number fields and date fields.
	 */
	GT(">"),
	/**
	 * GREATER THAN OR EQUAL. Can only be used for number fields and date
	 * fields.
	 */
	GTE(">="),
	/**
	 * Can only be used for number fields and date fields. When using the
	 * BETWEEN or NOT_BETWEEN operator in a {@link Condition}, the
	 * {@link Condition#getValue() value} property of the condition must be an
	 * array or a {@link Collection} object with exactly two elements. The first
	 * element is used as the "from" value (inclusive). The second element is
	 * used as the "to" value (inclusive). Although you can choose any
	 * {@link Collection} implementation you like, you should not choose one
	 * where you have no control over the order of the elements (like
	 * {@link HashSet}).
	 */
	BETWEEN,
	/**
	 * Can only be used for number fields and date fields.
	 */
	NOT_BETWEEN,
	/**
	 * 
	 */
	IN,
	/**
	 * 
	 */
	NOT_IN;

	/**
	 * Returns the {@link ComparisonOperator} corresponding to the specified
	 * string.
	 * 
	 * @param s
	 * @return
	 */
	public static ComparisonOperator parse(String s)
	{
		if (s != null) {
			for (ComparisonOperator op : values()) {
				if (op.symbol != null && s.equals(op.symbol)) {
					return op;
				}
				if (s.equalsIgnoreCase(op.name())) {
					return op;
				}
			}
		}
		return null;
	}

	private final String symbol;

	private ComparisonOperator()
	{
		this.symbol = null;
	}

	private ComparisonOperator(String symbol)
	{
		this.symbol = symbol;
	}

}
