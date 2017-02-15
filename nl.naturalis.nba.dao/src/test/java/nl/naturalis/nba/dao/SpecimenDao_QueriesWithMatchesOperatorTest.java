package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

/**
 * Tests queries with LIKE operator using the {@link SpecimenDao}.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDao_QueriesWithMatchesOperatorTest {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		deleteIndex(DocumentType.SPECIMEN);
		createIndex(DocumentType.SPECIMEN);
		createType(DocumentType.SPECIMEN);
		/*
		 * Insert 5 test specimens.
		 */
		pMajor = TestSpecimens.parusMajorSpecimen01();
		lFuscus1 = TestSpecimens.larusFuscusSpecimen01();
		lFuscus2 = TestSpecimens.larusFuscusSpecimen02();
		tRex = TestSpecimens.tRexSpecimen01();
		mSylvestris = TestSpecimens.malusSylvestrisSpecimen01();
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
	public void testQuery__01()
	{
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
			assertTrue(e.getMessage()
					.contains("Search term must not be null when using operator MATCHES"));
		}
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void testQuery__02() throws InvalidQueryException
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
		assertEquals("02", lFuscus1.getUnitID(), result.get(0).getUnitID());
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
	public void testQuery__04() throws InvalidQueryException
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
	public void testQuery__05() throws InvalidQueryException
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
	public void testQuery__06() throws InvalidQueryException
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
