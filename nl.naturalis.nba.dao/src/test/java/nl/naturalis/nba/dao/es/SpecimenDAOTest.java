package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.ESTestUtils.createIndex;
import static nl.naturalis.nba.dao.ESTestUtils.createType;
import static nl.naturalis.nba.dao.ESTestUtils.dropIndex;
import static nl.naturalis.nba.dao.ESTestUtils.refreshIndex;
import static nl.naturalis.nba.dao.ESTestUtils.saveObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAOTest {

	@Before
	public void before()
	{
		dropIndex(ESSpecimen.class);
		createIndex(ESSpecimen.class);
		createType(ESSpecimen.class);
	}

	@After
	public void after()
	{
		//dropIndex(ESSpecimen.class);
	}

	@Test
	public void testFindByUnitID_1()
	{
		ESSpecimen specimen = new ESSpecimen();
		String unitID = "ZMA.MAM.12345";
		specimen.setUnitID(unitID);
		saveObject(specimen);
		saveObject(specimen);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.findByUnitID(unitID);
		assertNotNull("01", result);
		assertEquals("02", 2, result.size());
		assertEquals("03", unitID, result.get(0).getUnitID());
		assertEquals("04", unitID, result.get(1).getUnitID());
		// Make sure no weird analysis stuff is going on
		result = dao.findByUnitID("ZMA");
		assertNotNull("05", result);
		assertEquals("06", 0, result.size());
		result = dao.findByUnitID("12345");
		assertNotNull("07", result);
		assertEquals("08", 0, result.size());
	}

	@Test
	public void testFindByUnitID_2()
	{
		ESSpecimen specimen = new ESSpecimen();
		String unitID0 = "L  0000123";
		specimen.setUnitID(unitID0);
		saveObject(specimen);
		refreshIndex(ESSpecimen.class);
		SpecimenDAO dao = new SpecimenDAO();
		List<Specimen> result = dao.findByUnitID(unitID0);
		assertNotNull("01", result);
		assertEquals("02", 1, result.size());
		assertEquals("03", unitID0, result.get(0).getUnitID());
		// Make sure no weird analysis stuff is going on
		result = dao.findByUnitID("L");
		assertNotNull("04", result);
		assertEquals("05", 0, result.size());
	}

}
