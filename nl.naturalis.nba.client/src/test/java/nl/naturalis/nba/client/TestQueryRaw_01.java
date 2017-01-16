package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;

import java.util.Map;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

public class TestQueryRaw_01 {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		TaxonClient client = session.getTaxonClient();
		QueryCondition condition1 = new QueryCondition("sourceSystemId", EQUALS_IC, "RGM.805582");
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
