package nl.naturalis.nba.api.annotations;

import nl.naturalis.nba.api.ComparisonOperator;

/**
 * Symbolic constants for the Elasticsearch analyzers defined within NBA's
 * document store.
 * 
 * @see Analyzers
 * 
 * @author Ayco Holleman
 *
 */
public enum Analyzer
{
	/**
	 * Indicates that a field is analysed using Elasticsearch's default
	 * analyzer.
	 */
	DEFAULT(null, "analyzed"),
	/**
	 * Indicates that a field can be queried using the
	 * {@link ComparisonOperator#EQUALS_IC EQUALS_IC} and
	 * {@link ComparisonOperator#NOT_EQUALS_IC NOT_EQUALS_IC} operators.
	 */
	CASE_INSENSITIVE("case_insensitive_analyzer", "ignoreCase"),
	/**
	 * Indicates that a field can be queried using the
	 * {@link ComparisonOperator#LIKE LIKE} and
	 * {@link ComparisonOperator#NOT_LIKE NOT_LIKE} operators.
	 */
	LIKE("like_analyzer", "like");

	private final String name;
	private final String multiFieldName;

	private Analyzer(String name, String multiFieldName)
	{
		this.name = name;
		this.multiFieldName = multiFieldName;
	}

	public String getName()
	{
		return name;
	}

	public String getMultiFieldName()
	{
		return multiFieldName;
	}

	@Override
	public String toString()
	{
		return name;
	}

}
