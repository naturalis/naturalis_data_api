package nl.naturalis.nba.api.annotations;

import nl.naturalis.nba.api.query.Operator;

public enum Analyzer
{
	NONE,
	/**
	 * Indicates that a field is analysed using Elasticsearch's default
	 * analyzer.
	 */
	DEFAULT,
	/**
	 * Indicates that a field can be queried using the {@link Operator#EQUALS_CI
	 * EQUALS_CI} and {@link Operator#NOT_EQUALS_CI NOT_EQUALS_CI}.
	 */
	CASE_INSENSITIVE,
	/**
	 * Indicates that a field can be queried using the {@link Operator#LIKE
	 * LIKE} and {@link Operator#NOT_LIKE NOT_LIKE} operators. Behind the scenes
	 * this results in the field being defined with a custom ngram analyzer.
	 */
	LIKE;

}
