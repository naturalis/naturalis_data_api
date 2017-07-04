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
		long start = System.currentTimeMillis();
		SpecimenDao dao = new SpecimenDao();
		ScientificNameGroupQuerySpec qs = new ScientificNameGroupQuerySpec();
		qs.setFrom(10000);
		qs.setSize(30);
		qs.setSpecimensSize(5);
		
		QueryCondition condition0 = new QueryCondition("collectionType", "=", "Botany");
		condition0.setConstantScore(false);
		qs.addCondition(condition0);
		
		dao.groupByScientificName(qs);
		System.out.println("********* groupByScientificName took: " + DaoUtil.getDuration(start));
	}

}
