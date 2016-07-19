package nl.naturalis.nba.api.query;

public class SortOption {

	private String field;
	private boolean ascending = true;

	public String getField()
	{
		return field;
	}

	public void setField(String field)
	{
		this.field = field;
	}

	public boolean isAscending()
	{
		return ascending;
	}

	public void setAscending(boolean ascending)
	{
		this.ascending = ascending;
	}

}
