package nl.naturalis.nba.dao.es;

import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

@SuppressWarnings("static-method")
public class SpecimenDwcaDaoTest {

	@Test
	public void testDynamic() throws InvalidQueryException, IOException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new Condition("sourceSystem.code", "=", "BRAHMS").or("sourceSystem.code",
				"=", "CRS"));
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/dwca.zip");
		dao.queryDynamic(qs, fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("dynamic took: " + took + " seconds");
	}

	//@Test
	public void testBotanyAll() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/botany-all.zip");
		dao.queryStatic("botany", "all", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("botany took: " + took + " seconds");
	}

	//@Test
	public void testMicropaleontology() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/micropaleontology.zip");
		dao.queryStatic("geology", "micropaleontology", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("micropaleontology took: " + took + " seconds");
	}

	//@Test
	public void testPaleontologyInvertebrates() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(
				"/home/ayco/tmp/paleontology-invertebrates.zip");
		dao.queryStatic("geology", "paleontology-invertebrates", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("paleontology-invertebrates took: " + took + " seconds");
	}

	//@Test
	public void testAmphibiaAndReptilia() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/amphibia-and-reptilia.zip");
		dao.queryStatic("zoology", "amphibia-and-reptilia", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("amphibia-and-reptilia took: " + took + " seconds");
	}

	//@Test
	public void testCainozoicMollusca() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/cainozoic-mollusca.zip");
		dao.queryStatic("zoology", "cainozoic-mollusca", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("cainozoic-mollusca took: " + took + " seconds");
	}

	//@Test
	public void testChelicerataAndMyriapoda() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/chelicerata-and-myriapoda.zip");
		dao.queryStatic("zoology", "chelicerata-and-myriapoda", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("chelicerata-and-myriapoda took: " + took + " seconds");
	}

	//@Test
	public void testColeoptera() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/coleoptera.zip");
		dao.queryStatic("zoology", "coleoptera", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("coleoptera took: " + took + " seconds");
	}

	//@Test
	public void testCollembola() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/collembola.zip");
		dao.queryStatic("zoology", "collembola", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("collembola took: " + took + " seconds");
	}

	//@Test
	public void testCrustacea() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/crustacea.zip");
		dao.queryStatic("zoology", "crustacea", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("crustacea took: " + took + " seconds");
	}

	//@Test
	public void testHymenoptera() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/hymenoptera.zip");
		dao.queryStatic("zoology", "hymenoptera", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("hymenoptera took: " + took + " seconds");
	}

	//@Test
	public void testLepidoptera() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/lepidoptera.zip");
		dao.queryStatic("zoology", "lepidoptera", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("lepidoptera took: " + took + " seconds");
	}

	//@Test
	public void testMammalia() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/mammalia.zip");
		dao.queryStatic("zoology", "mammalia", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("lepidoptera took: " + took + " seconds");
	}

	//@Test
	public void testMollusca() throws InvalidQueryException, IOException
	{
		SpecimenDwcaDao dao = new SpecimenDwcaDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/mollusca.zip");
		dao.queryStatic("zoology", "mollusca", fos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("testMollusca() took: " + took + " seconds");
	}

}
