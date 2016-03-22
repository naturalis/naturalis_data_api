package nl.naturalis.nba.elasticsearch.map;

public class Mapping extends Document {

	private final String dynamic = "strict";

	public Mapping()
	{
		super(null);
	}

	public String getDynamic()
	{
		return dynamic;
	}

}
