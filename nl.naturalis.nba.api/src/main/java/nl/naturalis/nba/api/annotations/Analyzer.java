package nl.naturalis.nba.api.annotations;

import nl.naturalis.nba.api.query.ComparisonOperator;

/**
 * Provides symbolic constants for the Elasticsearch analyzers defined within
 * NBA's document store.
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
	DEFAULT,
	/**
	 * Indicates that a field can be queried using the
	 * {@link ComparisonOperator#EQUALS_IC EQUALS_IC} and
	 * {@link ComparisonOperator#NOT_EQUALS_IC NOT_EQUALS_IC} operators.
	 */
	CASE_INSENSITIVE,
	/**
	 * Indicates that a field can be queried using the
	 * {@link ComparisonOperator#LIKE LIKE} and
	 * {@link ComparisonOperator#NOT_LIKE NOT_LIKE} operators.
	 */
	LIKE;

}
