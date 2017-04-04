package nl.naturalis.nba.api.model.metadata;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.QuerySpec;

/**
 * Enumerates all publicly available configuration settings for the NBA.
 * 
 * @author Ayco Holleman
 *
 */
public enum NbaSetting
{
	/**
	 * &#34;operator.LIKE.min.term.length&#34;. The minimum length of a search
	 * term when using operator {@link ComparisonOperator#LIKE} or
	 * {@link ComparisonOperator#NOT_LIKE}.
	 */
	OPERATOR_LIKE_MIN_TERM_LENGTH("operator.LIKE.min_term_length"),
	/**
	 * &#34;operator.LIKE.max.term.length&#34;. The maximum length of a search
	 * term when using operator {@link ComparisonOperator#LIKE} or
	 * {@link ComparisonOperator#NOT_LIKE}.
	 */
	OPERATOR_LIKE_MAX_TERM_LENGTH("operator.LIKE.max_term_length"),

	/**
	 * &#34;index.max_result_window&#34;. The size of the result window. When
	 * specifying an NBA query using a {@link QuerySpec}, the sum of
	 * {@link QuerySpec#getFrom() from} plus {@link QuerySpec#getSize()} must
	 * never exceed this number.
	 */
	INDEX_MAX_RESULT_WINDOW("index.max_result_window");

	private String name;

	private NbaSetting(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

}
