package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.LIKE;

import nl.naturalis.nba.api.IMultiMediaObjectAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

public class Demo {

	public static void main(String[] args) throws InvalidQueryException
	{
		// Start an NBA session
		//String baseUrl = "http://localhost:8080/v2";
		String baseUrl = "http://145.136.242.164:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		
//		ISpecimenAccess client = session.getSpecimenClient();		
//		String field = "identifications.defaultClassification.family";
//		QueryCondition condition = new QueryCondition(field, "!=", null);
//		QuerySpec query = new QuerySpec();
//		query.addCondition(condition);
//		query.setFields(Arrays.asList(new Path("sourceSystem.name")));
//		QueryResult<?> result = client.query(query);
//		ClientUtil.printTerse(result);
		
		IMultiMediaObjectAccess client = session.getMultiMediaObjectClient();
		QueryCondition condition = new QueryCondition("unitID", LIKE, "AVES.147969");
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		QueryResult<?> result = client.query(query);
		ClientUtil.printTerse(result);
		
//		ITaxonAccess client = session.getTaxonClient();
//		String field = "acceptedName.genusOrMonomial";
//		QueryCondition condition = new QueryCondition(field, "!=", null);
//		QuerySpec query = new QuerySpec();
//		query.addCondition(condition);
		
	}

}
