package nl.naturalis.nba.elasticsearch.map;

public enum Index
{
	ANALYZED, NOT_ANALYZED, NO;

	public String toString()
	{
		return name().toLowerCase();
	}
}
