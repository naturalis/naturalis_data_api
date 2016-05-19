package nl.naturalis.nba.api.query;

/**
 * Specifies all operators that can be used in a {@link Condition query
 * condition}.
 * 
 * @author Ayco Holleman
 *
 */
public enum Operator
{

	/**
	 * Operator to be used when checking for strict equality between search term
	 * and field value. For alpha-numeric fields the search term and field value
	 * are compared in a <i>case sensitive</i> way.
	 */
	EQUALS("="),
	/**
	 * Operator to be used when checking for strict in-equality between search
	 * term and field value. For alpha-numeric fields the search term and field
	 * value are compared in a <i>case sensitive</i> way.
	 */
	NOT_EQUALS("!="),
	/**
	 * EQUALS IGNORE CASE. Operator to be used when checking for equality while
	 * ignoring case. Can only be used for string fields.
	 */
	EQUALS_IC,
	/**
	 * NOT EQUALS IGNORE CASE. Operator to be used when checking for in-equality
	 * while ignoring case. Can only be used for string fields.
	 */
	NOT_EQUALS_CI,
	/**
	 * Operator to be used for checking if the search term is a substring of the
	 * field value. Can only be used for string fields. This is akin to the SQL
	 * LIKE operator, but you don't use the percentage sign (%) in the search
	 * term.
	 */
	LIKE,
	/**
	 * Operator to be used for checking if the search term is not a substring of
	 * the field value. Can only be used for string fields. This is akin to the
	 * SQL LIKE operator, but you don't use the percentage sign (%) in the
	 * search term.
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
	 * Can only be used for number fields and date fields.
	 */
	BETWEEN,
	/**
	 * Can only be used for number fields and date fields.
	 */
	NOT_BETWEEN;

	/**
	 * Returns the {@link Operator} corresponding to the specified string.
	 * 
	 * @param s
	 * @return
	 */
	public static Operator parse(String s)
	{
		for (Operator op : values()) {
			if (op.symbol != null && s.equals(op.symbol)) {
				return op;
			}
			if (s.equalsIgnoreCase(op.name())) {
				return op;
			}
		}
		return null;
	}

	private final String symbol;

	private Operator()
	{
		this.symbol = null;
	}

	private Operator(String symbol)
	{
		this.symbol = symbol;
	}

}
