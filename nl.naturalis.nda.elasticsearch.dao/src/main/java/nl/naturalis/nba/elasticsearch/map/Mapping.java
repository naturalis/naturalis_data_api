package nl.naturalis.nba.elasticsearch.map;


public class Mapping extends ESObject {

	private final String dynamic = "strict";

	Mapping()
	{
	}

	String getDynamic()
	{
		return dynamic;
	}

}
