package nl.naturalis.nba.elasticsearch.map;


public class Mapping extends ESObject {

	private final String dynamic = "strict";

	public Mapping()
	{
	}

	public String getDynamic()
	{
		return dynamic;
	}

}
