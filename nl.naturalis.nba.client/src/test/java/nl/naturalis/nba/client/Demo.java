package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.api.LogicalOperator.*;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

public class Demo {

	public static void main(String[] args) throws InvalidQueryException
	{
		// Start een NBA sessie
		String baseUrl = "http://localhost:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();

		// Criterium voor de vindplaats v.e. specimen
		String field1 = "gatheringEvent.localityText";
		QueryCondition condition1 = new QueryCondition(field1, MATCHES, "France");

		// Criterium voor de wetenschappelijke naam
		String field2 = "identifications.scientificName.fullScientificName";
		QueryCondition condition2 = new QueryCondition(field2, MATCHES, "benthamiana");
		
		QuerySpec query = new QuerySpec();
		query.addCondition(condition1);
		query.addCondition(condition2);
		query.setLogicalOperator(OR);
		query.setSize(1);

		QueryResult<Specimen> result = client.query(query);
		ClientUtil.printTerse(result);
	}

}
