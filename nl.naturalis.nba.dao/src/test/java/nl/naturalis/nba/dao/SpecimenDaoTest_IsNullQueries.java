package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchResult;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.model.Specimen;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_IsNullQueries {

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

	/*
	 * Test with condition that tests for null on string field.
	 */
	@Test
	public void test__01() throws InvalidQueryException
	{
		// UnitGUID is null in all test specimens, so query should return them
		// all.
		SearchCondition condition = new SearchCondition("unitGUID", EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 5, result.size());
	}

	/*
	 * Test with a negated condition that tests for null on string field.
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		// UnitGUID is always null, so query should return 0 specimens.
		SearchCondition condition = new SearchCondition(NOT, "unitGUID", EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 0, result.size());
	}

	/*
	 * Test with a condition that tests for not null on string field.
	 */
	@Test
	public void test__03() throws InvalidQueryException
	{
		SearchCondition condition = new SearchCondition("unitGUID", NOT_EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 0, result.size());
	}

	/*
	 * Test with a negated condition that tests for not null on string field.
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		SearchCondition condition = new SearchCondition(NOT, "unitGUID", NOT_EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 5, result.size());
	}

	/*
	 * Test with a negated condition that tests for not null on a date field.
	 */
	@Test
	public void test__05() throws InvalidQueryException
	{
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		// Only for mSylvestris is gatheringEvent.dateTimeBegin null.
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with a condition that tests for null on number field.
	 */
	@Test
	public void test__06() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		SearchCondition condition = new SearchCondition(field, EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, so only
		// tRex and mSylvestris (2 specimens) should be returned.
		assertEquals("01", 2, result.size());
	}

	/*
	 * Test with a condition that tests for not null on number field.
	 */
	@Test
	public void test__07() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		SearchCondition condition = new SearchCondition(field, NOT_EQUALS, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, the other specimens don't.
		assertEquals("01", 3, result.size());
	}

	/*
	 * Test with double negative.
	 */
	@Test
	public void test__08() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		SearchCondition condition = new SearchCondition(NOT, field, NOT_EQUALS, null);
		// In other words: field EQUALS null
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, so only
		// tRex and mSylvestris (2 specimens) should be returned.
		assertEquals("01", 2, result.size());
	}
}
