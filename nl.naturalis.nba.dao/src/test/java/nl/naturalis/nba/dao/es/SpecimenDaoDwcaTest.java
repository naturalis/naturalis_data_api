package nl.naturalis.nba.dao.es;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

@SuppressWarnings("static-method")
public class SpecimenDaoDwcaTest {

	//@Test
	public void testDynamic() throws InvalidQueryException, IOException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("sourceSystem.code", "=", "BRAHMS"));
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/dwca.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaQuery(qs, zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("dynamic took: " + took + " seconds");
	}

	//@Test
	public void testNSR() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/botany.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("botany", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("botany took: " + took + " seconds");
	}

}
