package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Tests queries with LIKE operator using the {@link SpecimenDao}.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDao_QueriesWithLikeOperatorTest {

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
		ESTestUtils.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
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
		QueryCondition condition = new QueryCondition(name, LIKE, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		try {
			dao.query(qs);
			fail("Expected an InvalidQueryException");
		}
		catch (InvalidQueryException e) {
			assertTrue(e.getMessage()
					.contains("Search term must not be null when using operator LIKE"));
		}
	}

	/*
	 * Test with comparison on field within or descending from a "nested" object
	 * (both identifications as systemClassification have data type "nested").
	 */
	@Test
	public void testQuery__02() throws InvalidQueryException
	{
		String rank = "identifications.systemClassification.rank";
		String name = "identifications.systemClassification.name";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(rank, LIKE, "ingdo")); /* kingdom */
		qs.addCondition(new QueryCondition(name, LIKE, "nimali")); /* Animalia */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Only mSylvestris is a plant, so should get 4 specimens
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test with odd characters
	 */
	@Test
	public void testQuery__03() throws InvalidQueryException
	{
		String author = "identifications.scientificName.authorshipVerbatim";
		QuerySpec qs = new QuerySpec();
		// Should yield mSylvestris only:
		qs.addCondition(new QueryCondition(author, LIKE, ".) Mill."));
		/* (L.) Mill. */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters
	 */
	@Test
	public void testQuery__04() throws InvalidQueryException
	{
		String author = "identifications.scientificName.authorshipVerbatim";
		QuerySpec qs = new QuerySpec();
		// Should yield tRex only:
		qs.addCondition(
				new QueryCondition(author, LIKE, "orn, 190")); /* Osborn, 1905 */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters and NOT_LIKE
	 */
	@Test
	public void testQuery__05() throws InvalidQueryException
	{
		String author = "identifications.scientificName.authorshipVerbatim";
		QuerySpec qs = new QuerySpec();
		// Should yield everything but tRex (4 specimens)
		qs.addCondition(new QueryCondition(author, NOT_LIKE, "orn, 190"));
		/* Osborn, 1905 */
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test with NOT_LIKE and multi-valued field. NOT_LIKE is expected to mean:
	 * NONE of the values of the multi-valued field are LIKE the search term.
	 */
	@Test
	public void testQuery__06() throws InvalidQueryException
	{
		String name = "identifications.systemClassification.name";
		QuerySpec qs = new QuerySpec();
		/*
		 * NB so in fact we are asking here for specimens where NONE of the
		 * monomials in the system classification (kingdom, phylum, class, etc)
		 * is LIKE Animalia! (But of course that's still only mSylvestris - the
		 * only plant in the test specimens).
		 */
		qs.addCondition(new QueryCondition(name, NOT_LIKE, "Animalia"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
		assertEquals("02", "L   100", result.iterator().next().getUnitID());
	}

	/*
	 * Make sure LIKE ignores case.
	 */
	@Test
	public void testQuery__07() throws InvalidQueryException
	{
		String name = "identifications.systemClassification.name";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(name, LIKE, "animal"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Make sure LIKE ignores case.
	 */
	@Test
	public void testQuery__08() throws InvalidQueryException
	{
		String name = "identifications.systemClassification.name";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(name, NOT_LIKE, "animal"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

}
