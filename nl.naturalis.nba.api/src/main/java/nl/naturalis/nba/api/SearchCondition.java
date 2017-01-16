package nl.naturalis.nba.api;

import java.util.List;

public class SearchCondition {

	private UnaryBooleanOperator not;
	private List<SearchField> fields;
	private ComparisonOperator operator;
	private List<SearchCondition> and;
	private List<SearchCondition> or;

}
