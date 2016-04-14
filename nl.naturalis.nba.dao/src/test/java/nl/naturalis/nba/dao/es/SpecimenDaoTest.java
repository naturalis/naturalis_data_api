package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.dao.ESTestUtils.createIndex;
import static nl.naturalis.nba.dao.ESTestUtils.createType;
import static nl.naturalis.nba.dao.ESTestUtils.dropIndex;
import static nl.naturalis.nba.dao.ESTestUtils.saveObject;
import static nl.naturalis.nba.dao.ESTestUtils.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDaoTest {

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
	public void testFindByUnitID()
	{
		ESSpecimen specimen = new ESSpecimen();
		String unitID0 = "L  12345";
		String unitID1 = "ZMA.MAM.12345";
		specimen.setUnitID(unitID0);
		saveObject(specimen);
		specimen.setUnitID(unitID1);
		saveObject(specimen);
		saveObject(specimen);
		sleep();
		SpecimenDao dao = new SpecimenDao();
		List<Specimen> result = dao.findByUnitID(unitID0);
		assertNotNull("01", result);
		assertEquals("02", 1, result.size());
		assertEquals("03", unitID0, result.get(0).getUnitID());
		result = dao.findByUnitID(unitID1);
		assertNotNull("01", result);
		assertEquals("02", 2, result.size());
		assertEquals("03", unitID1, result.get(0).getUnitID());
		assertEquals("04", unitID1, result.get(1).getUnitID());
	}

}
