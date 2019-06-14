package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.SpecimenMock;

/**
 * Tests queries with LIKE operator using the {@link SpecimenDao}.
 * 
 * @author Ayco Holleman
 *
 */
public class SpecimenDaoTest_Matches {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_Matches.class);

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		logger.info("Starting tests");
		deleteIndex(DocumentType.SPECIMEN);
		createIndex(DocumentType.SPECIMEN);
		/*
		 * Insert 5 test specimens.
		 */
		pMajor = SpecimenMock.parusMajorSpecimen01();
		lFuscus1 = SpecimenMock.larusFuscusSpecimen01();
		lFuscus2 = SpecimenMock.larusFuscusSpecimen02();
		tRex = SpecimenMock.tRexSpecimen01();
		mSylvestris = SpecimenMock.malusSylvestrisSpecimen01();
		DaoTestUtil.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@After
	public void after()
	{
		// dropIndex(Specimen.class);
	}

	/*
	 * Test with null (should get error).
	 */
	@Test
	public void test__01()
	{
		String expecting = "Search value must not be null when using operator MATCHES";
		String name = "identifications.systemClassification.name";
		QueryCondition condition = new QueryCondition(name, MATCHES, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		try {
			dao.query(qs);
			fail("Expected an InvalidQueryException");
		}
		catch (InvalidQueryException e) {
			assertTrue(e.getMessage().contains(expecting));
		}
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, MATCHES, "bossen"));
		/*
		 * That's larusFuscusSpecimen01 : "bossen" MATCHES
		 * "In de bossen nabij Aalten"
		 */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
		String unitID = result.get(0).getItem().getUnitID();
		assertEquals("02", lFuscus1.getUnitID(), unitID);
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void testQuery__03() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, NOT_MATCHES, "bossen"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, MATCHES, "   bossen  one two three"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__05() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, MATCHES, "   nabij"));
		/*
		 * That's larusFuscusSpecimen01 &parusMajorSpecimen01
		 */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 2, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__06() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, NOT_MATCHES, "   nabij"));
		/*
		 * That's all but larusFuscusSpecimen01 &parusMajorSpecimen01
		 */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}
}
