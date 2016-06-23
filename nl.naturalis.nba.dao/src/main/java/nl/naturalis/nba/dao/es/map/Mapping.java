package nl.naturalis.nba.dao.es.map;

/**
 * Class representing an Elasticsearch type mapping.
 * 
 * @see Document
 * 
 * @author Ayco Holleman
 *
 */
public class Mapping extends Document {

	/* NBA types are always strictly typed. */
	private final String dynamic = "strict";

	public String getDynamic()
	{
		return dynamic;
	}

}
