package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

public class Demo {

	public static void main(String[] args)
	{

		String baseUrl = "http://localhost:8080/v2";
		//String baseUrl = "http://145.136.242.164:8080/v2";
		
		ClientConfig config = new ClientConfig();
		config.setBaseUrl(baseUrl);
		
		NbaSession session = new NbaSession(config);
		System.out.println(session.ping());
		
		
		

	}

}
