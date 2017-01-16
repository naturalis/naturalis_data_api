package nl.naturalis.nba.client;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

public class TestEqualsQuery_01 {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();
		QueryCondition condition1 = new QueryCondition("multiMediaPublic", "!=", false);
		QuerySpec query = new QuerySpec();
		query.addFields("unitID", "multiMediaPublic");
		query.addCondition(condition1);
		QueryResult<Specimen> result = null;
		try {
			result = client.query(query);
		}
		catch (InvalidQueryException e) {
			System.err.println(e.getMessage());
		}
		ClientUtil.printTerse(result);
		System.out.println("Number of specimens found: " + result.size());
	}

}
