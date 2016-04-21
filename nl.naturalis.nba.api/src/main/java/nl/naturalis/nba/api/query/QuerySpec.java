package nl.naturalis.nba.api.query;

import java.util.List;

public class QuerySpec {

	private Criterion criterion;
	private List<String> sortBy;

	public QuerySpec()
	{
	}

	public Criterion getCriterion()
	{
		return criterion;
	}

	public void setCriterion(Criterion criterion)
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
