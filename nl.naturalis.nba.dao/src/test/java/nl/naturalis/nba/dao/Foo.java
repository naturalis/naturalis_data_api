package nl.naturalis.nba.dao;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.ScientificNameGroupQuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup2;
import nl.naturalis.nba.common.json.JsonUtil;

public class Foo {

	public static void main(String[] args) throws InvalidQueryException
	{
		SpecimenDao dao = new SpecimenDao();
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		//QueryCondition condition0 = new QueryCondition("recordBasis", "=", "PreservedSpecimen");
		QueryCondition condition0 = new QueryCondition(
				"identifications.scientificName.scientificNameGroup", "=", "larus fuscus");
		qs.addCondition(condition0);
		QueryResult<ScientificNameGroup2> result = dao.groupByScientificName(qs);
		//System.out.println(JsonUtil.toPrettyJson(result));
	}

}
