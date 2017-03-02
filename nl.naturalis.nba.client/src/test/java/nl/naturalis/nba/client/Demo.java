package nl.naturalis.nba.client;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.api.LogicalOperator.*;

import java.io.InputStream;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.utils.StringUtil;

public class Demo {

	public static void main(String[] args) throws InvalidQueryException
	{
		// Start een NBA sessie
		String baseUrl = "http://145.136.242.167:8080/v2";
		NbaSession session = new NbaSession(new ClientConfig(baseUrl));
		SpecimenClient client = session.getSpecimenClient();

		String field = "gatheringEvent.siteCoordinates.geoShape";
		InputStream is = Demo.class.getResourceAsStream("aalten.geojson.txt");
		String geoJson = StringUtil.fromInputStream(is);
		QueryCondition condition = new QueryCondition(field, IN, geoJson);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);

		query.setLogicalOperator(OR);
		query.setSize(1);

		QueryResult<Specimen> result = client.query(query);
		ClientUtil.printTerse(result);
	}

}
