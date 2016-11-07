package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS_IC;

import java.util.Map;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

public class TestQueryRaw_01 {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		Condition condition1 = new Condition("sourceSystemId", EQUALS_IC, "RGM.805582");
		QuerySpec query = new QuerySpec();
		query.addFields("recordURI");
		query.addCondition(condition1);
		QueryResult<Map<String, Object>> result = null;
		try {
			result = client.queryData(query);
		}
		catch (InvalidQueryException e) {
			System.err.println(e.getMessage());
		}
		ClientUtil.printTerse(result);
	}

}
