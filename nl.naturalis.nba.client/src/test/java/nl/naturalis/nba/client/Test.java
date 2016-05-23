package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.query.Operator.EQUALS;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class Test {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NBASession session = new NBASessionConfigurator().setBaseUrl(baseUrl).create();
		String answer = session.ping();
		System.out.println(answer);
	}

}
