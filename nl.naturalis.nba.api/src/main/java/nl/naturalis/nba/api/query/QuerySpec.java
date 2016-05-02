package nl.naturalis.nba.api.query;

import java.util.List;

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
