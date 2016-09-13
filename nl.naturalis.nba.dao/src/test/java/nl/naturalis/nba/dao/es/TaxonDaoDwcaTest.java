package nl.naturalis.nba.dao.es;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

@SuppressWarnings("static-method")
public class TaxonDaoDwcaTest {

	//@Test
	public void testDynamic() throws InvalidQueryException, IOException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("sourceSystem.code", "=", "BRAHMS"));
		TaxonDao dao = new TaxonDao();
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

	@Test
	public void testNSR() throws IOException, NoSuchDataSetException
	{
		TaxonDao dao = new TaxonDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/nsr.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("nsr", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("NSR took: " + took + " seconds");
	}

}
