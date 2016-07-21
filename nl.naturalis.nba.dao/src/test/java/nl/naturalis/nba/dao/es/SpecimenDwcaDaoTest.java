package nl.naturalis.nba.dao.es;

import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class SpecimenDwcaDaoTest {

	@Test
	public void testQuerySpecimens_01() throws InvalidQueryException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("sourceSystem.code", "=", "CRS"));
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		dao.querySpecimens(qs);
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("Took: " + took);
	}

}
