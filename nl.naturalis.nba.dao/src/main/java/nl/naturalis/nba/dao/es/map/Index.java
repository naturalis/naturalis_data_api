package nl.naturalis.nba.dao.es.map;

public enum Index
{
	ANALYZED, NOT_ANALYZED, NO;

	public String toString()
	{
		return name().toLowerCase();
	}
}
