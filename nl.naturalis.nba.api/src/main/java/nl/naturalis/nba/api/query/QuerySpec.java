package nl.naturalis.nba.api.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a query specification. All information required by the various
 * {@code query} methods in the API take there input from a {@link QuerySpec}
 * instance.
 * 
 * @author Ayco Holleman
 *
 */
public class QuerySpec {

	private List<Condition> conditions;
	private LogicalOperator logicalOperator;
	private List<String> sortBy;

	public QuerySpec()
	{
	}

	public void addCondition(Condition condition)
	{
		if (conditions == null) {
			conditions = new ArrayList<>(5);
		}
		conditions.add(condition);
	}

	public List<Condition> getConditions()
	{
		return conditions;
	}

	public void setConditions(List<Condition> conditions)
	{
		this.conditions = conditions;
	}

	public LogicalOperator getLogicalOperator()
	{
		return logicalOperator == null ? AND : logicalOperator;
	}

	public void setLogicalOperator(LogicalOperator operator)
	{
		this.logicalOperator = operator;
	}

	public List<String> getSortBy()
	{
		return sortBy;
	}

	public void setSortBy(List<String> sortBy)
	{
		this.sortBy = sortBy;
	}

}
