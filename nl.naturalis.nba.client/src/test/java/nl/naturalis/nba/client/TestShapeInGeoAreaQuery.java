package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.IN;

import java.io.FileNotFoundException;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

public class TestShapeInGeoAreaQuery {

	public static void main(String[] args) throws FileNotFoundException, InvalidQueryException
	{
		String baseUrl = "http://145.136.242.170:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();
		QuerySpec querySpec = new QuerySpec();
		QueryCondition condition = new QueryCondition("gatheringEvent.siteCoordinates.geoShape", IN,
				"1004050@GEO");
		querySpec.addCondition(condition);
		ClientUtil.printTerse(querySpec);
		QueryResult<Specimen> result = client.query(querySpec);
		ClientUtil.printTerse(result);
	}

}
