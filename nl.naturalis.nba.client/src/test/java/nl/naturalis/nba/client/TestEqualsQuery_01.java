package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS_IC;

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
		Condition condition1 = new Condition("sourceSystemId", EQUALS_IC, "RGM.805582");
		QuerySpec query = new QuerySpec();
		query.addFields("unitID");
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
