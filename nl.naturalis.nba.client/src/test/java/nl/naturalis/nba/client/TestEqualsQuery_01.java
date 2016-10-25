package nl.naturalis.nba.client;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

public class TestEqualsQuery_01 {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();
		Condition condition1 = new Condition("multiMediaPublic", "!=", false);
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
