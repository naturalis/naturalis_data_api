package nl.naturalis.nba.dao.es;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

public class SpecimenDwcaDaoTest {

	@Test
	public void testQuerySpecimens_01() throws InvalidQueryException, IOException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("sourceSystem.code", "=", "BRAHMS"));
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/dwca.zip");
		dao.queryDynamic(qs, fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("Took: " + took);
	}

}
