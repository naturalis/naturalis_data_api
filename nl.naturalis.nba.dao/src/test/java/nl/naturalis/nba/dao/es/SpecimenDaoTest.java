package nl.naturalis.nba.dao.es;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.dao.ESTestUtils;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDaoTest {

	private static final String TEST_INDEX = "nba-test";

	@Before
	public void before()
	{
		ESTestUtils.createIndex(TEST_INDEX);
		ESTestUtils.createType(TEST_INDEX, "Specimen", ESSpecimen.class);
	}

	@After
	public void after()
	{
		ESTestUtils.dropIndex(TEST_INDEX);
	}

	@Test
	public void testFindByUnitID()
	{
		// ...
	}

}
