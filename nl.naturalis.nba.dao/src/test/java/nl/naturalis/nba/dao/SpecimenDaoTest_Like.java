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
public class SpecimenDaoTest_Like {

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
	public void testWithNullValue_01()
	{
		String expecting = "Search value must not be null when using operator LIKE";
		String field = "identifications.scientificName.genusOrMonomial";
		QueryCondition condition = new QueryCondition(field, LIKE, null);
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
	 * Test with query on field within "nested" object.
	 */
	@Test
	public void testWithNestedField_01() throws InvalidQueryException
	{
		String field0 = "identifications.scientificName.genusOrMonomial";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field0, LIKE, "aru"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Larus fuscus (twice) and Parus major
		assertEquals("01", 3, result.size());
	}

	/*
	 * Test with query on multiple fields within "nested" object.
	 */
	@Test
	public void testWithMultipleNestedFields_01() throws InvalidQueryException
	{
		String field0 = "identifications.scientificName.genusOrMonomial";
		String field1 = "identifications.scientificName.specificEpithet";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field0, LIKE, "aru"));
		qs.addCondition(new QueryCondition(field1, LIKE, "ajor"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Parus major!
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters
	 */
	@Test
	public void testWithNonLettersInValue_01() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field, LIKE, "Dorchester,"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// M. sylvestris!
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters
	 */
	@Test
	public void testWithNonLettersInValue_02() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field, LIKE, ", U.S.A"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// T. rex!
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters and NOT_LIKE
	 */
	@Test
	public void testWithNotLike_01() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field, NOT_LIKE, "Dorchester,"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// All but M. sylvestris
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test with NOT_LIKE and multi-valued field. NOT_LIKE is expected to mean:
	 * NONE of the values of the multi-valued field are LIKE the search term.
	 */
	@Test
	public void testWithNotLikeOnArrayOrList() throws InvalidQueryException
	{
		/*
		 * Can't do this test any longer because we don't have multi-valued
		 * fields (lists or arrays) with the like_analyzer any longer.
		 */
	}

	/*
	 * Make sure LIKE ignores case.
	 */
	@Test
	public void testLikeIgnoresCase_01() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field, LIKE, "dorchester"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// M. sylvestris!
		assertEquals("01", 1, result.size());
	}

	/*
	 * Make sure NOT_LIKE ignores case.
	 */
	@Test
	public void testNotLikeIgnoresCase_01() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		QuerySpec qs = new QuerySpec();
		qs.addCondition(new QueryCondition(field, NOT_LIKE, "dorchester"));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// All but M. sylvestris
		assertEquals("01", 4, result.size());
	}

}
