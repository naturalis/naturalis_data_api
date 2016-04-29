package nl.naturalis.nba.api.query;

import java.util.List;

public class QuerySpec {

	private Condition criterion;
	private List<String> sortBy;

	public QuerySpec()
	{
	}

	public Condition getCriterion()
	{
		return criterion;
	}

	public void setCriterion(Condition criterion)
	{
		this.criterion = criterion;
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
