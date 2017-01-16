package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.*;

import java.util.Map;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

public class TestEqualsIcQuery_01 {

	public static void main(String[] args)
	{
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();
		QueryCondition condition1 = new QueryCondition("gatheringEvent.localityText", NOT_LIKE, "ji; Ro");
		QuerySpec query = new QuerySpec();
		query.addCondition(condition1);
		//QueryResult<Specimen> result = null;
		QueryResult<Map<String,Object>> result = null;
		try {
			result = client.queryData(query);
		}
		catch (InvalidQueryException e) {
			System.err.println(e.getMessage());
		}
		ClientUtil.printTerse(result);
	}

}
