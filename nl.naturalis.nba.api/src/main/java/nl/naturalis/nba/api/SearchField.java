package nl.naturalis.nba.api;

public class SearchField {

	private String path;
	private Float boost;

	public SearchField()
	{
	}

	public SearchField(String path)
	{
		this.path = path;
	}

	public SearchField(String path, float boost)
	{
		this.path = path;
		this.boost = boost;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public Float getBoost()
	{
		return boost;
	}

	public void setBoost(Float boost)
	{
		this.boost = boost;
	}

}
