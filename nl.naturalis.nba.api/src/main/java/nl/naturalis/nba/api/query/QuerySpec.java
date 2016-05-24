package nl.naturalis.nba.api.query;

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

	private Condition condition;
	private List<String> sortBy;

	public QuerySpec()
	{
	}

	public Condition getCondition()
	{
		return condition;
	}

	public void setCondition(Condition condition)
	{
		this.condition = condition;
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
