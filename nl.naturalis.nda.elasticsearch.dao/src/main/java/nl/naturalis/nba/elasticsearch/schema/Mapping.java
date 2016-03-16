package nl.naturalis.nba.elasticsearch.schema;


class Mapping extends ESObject {

	private final String dynamic = "strict";

	Mapping()
	{
	}

	String getDynamic()
	{
		return dynamic;
	}

}
