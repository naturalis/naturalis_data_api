package nl.naturalis.nba.dao.es.map;

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
