package nl.naturalis.nba.client;

import java.util.Arrays;

import nl.naturalis.nba.api.ISpecimenAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;

public class Demo {

	public static void main(String[] args) throws InvalidQueryException
	{
		// Start an NBA session
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		
		ISpecimenAccess client = session.getSpecimenClient();		
		String field = "identifications.defaultClassification.family";
		QueryCondition condition = new QueryCondition(field, "!=", null);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		query.setFields(Arrays.asList(new Path("sourceSystem.name")));
		
//		ITaxonAccess client = session.getTaxonClient();
//		String field = "acceptedName.genusOrMonomial";
//		QueryCondition condition = new QueryCondition(field, "!=", null);
//		QuerySpec query = new QuerySpec();
//		query.addCondition(condition);
		
		QueryResult<?> result = client.query(query);
		ClientUtil.printTerse(result);
	}

}
