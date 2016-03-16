package nl.naturalis.nba.elasticsearch.schema;

public enum Index
{
	NOT_ANALYZED;

	public String toString()
	{
		return name().toLowerCase();
	}
}
