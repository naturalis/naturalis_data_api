package nl.naturalis.nba.api.search;

import java.util.List;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.query.UnaryBooleanOperator;

public class SearchCondition {

	private UnaryBooleanOperator not;
	private List<SearchField> fields;
	private ComparisonOperator operator;
	private List<SearchCondition> and;
	private List<SearchCondition> or;

}
