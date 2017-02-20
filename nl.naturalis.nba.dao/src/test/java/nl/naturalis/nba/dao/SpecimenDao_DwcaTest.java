package nl.naturalis.nba.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.dao.SpecimenDao;

@SuppressWarnings("static-method")
public class SpecimenDao_DwcaTest {

	//@Test
	public void testDynamic() throws InvalidQueryException, IOException
	{
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition("sourceSystem.code", "=", "BRAHMS"));
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
	public void testBotanyAll() throws InvalidQueryException, IOException
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

	//@Test
	public void testMicropaleontology() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/micropaleontology.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("micropaleontology", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("micropaleontology took: " + took + " seconds");
	}

	//@Test
	public void testPaleontologyInvertebrates() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream(
				"/home/ayco/tmp/paleontology-invertebrates.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("paleontology-invertebrates", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("paleontology-invertebrates took: " + took + " seconds");
	}

	//@Test
	public void testAmphibiaAndReptilia() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/amphibia-and-reptilia.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("amphibia-and-reptilia", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("amphibia-and-reptilia took: " + took + " seconds");
	}

	//@Test
	public void testCainozoicMollusca() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/cainozoic-mollusca.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("cainozoic-mollusca", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("cainozoic-mollusca took: " + took + " seconds");
	}

	//@Test
	public void testChelicerataAndMyriapoda() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/chelicerata-and-myriapoda.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("chelicerata-and-myriapoda", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("chelicerata-and-myriapoda took: " + took + " seconds");
	}

	@Test
	public void testColeoptera() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/coleoptera.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("coleoptera", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("coleoptera took: " + took + " seconds");
	}

	//@Test
	public void testCollembola() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/collembola.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("collembola", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("collembola took: " + took + " seconds");
	}

	//@Test
	public void testCrustacea() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/crustacea.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("crustacea", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("crustacea took: " + took + " seconds");
	}

	//@Test
	public void testHymenoptera() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/hymenoptera.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("hymenoptera", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("hymenoptera took: " + took + " seconds");
	}

	//@Test
	public void testLepidoptera() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/lepidoptera.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("lepidoptera", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("lepidoptera took: " + took + " seconds");
	}

	//@Test
	public void testMammalia() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/mammalia.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("mammalia", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("lepidoptera took: " + took + " seconds");
	}

	//@Test
	public void testMollusca() throws InvalidQueryException, IOException
	{
		SpecimenDao dao = new SpecimenDao();
		long start = System.currentTimeMillis();
		FileOutputStream fos = new FileOutputStream("/home/ayco/tmp/mollusca.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		dao.dwcaGetDataSet("mollusca", zos);
		fos.close();
		System.out.println();
		long end = System.currentTimeMillis();
		long took = (end - start) / 1000;
		System.out.println("testMollusca() took: " + took + " seconds");
	}

}
