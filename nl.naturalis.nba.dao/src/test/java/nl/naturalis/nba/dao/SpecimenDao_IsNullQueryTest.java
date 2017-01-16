package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/**
 * Tests INbaAccess.query for conditions that either test for NULL or NOT NULL.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDao_IsNullQueryTest {

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

	/*
	 * Test with condition that tests for null on string field.
	 */
	@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		// UnitGUID is null in all test specimens, so query should return them
		// all.
		QueryCondition condition = new QueryCondition("unitGUID", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 5, result.size());
	}

	/*
	 * Test with a negated condition that tests for null on string field.
	 */
	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		// UnitGUID is always null, so query should return 0 specimens.
		QueryCondition condition = new QueryCondition(NOT, "unitGUID", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	/*
	 * Test with a condition that tests for not null on string field.
	 */
	@Test
	public void testQuery__QuerySpec__03() throws InvalidQueryException
	{
		QueryCondition condition = new QueryCondition("unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	/*
	 * Test with a negated condition that tests for not null on string field.
	 */
	@Test
	public void testQuery__QuerySpec__04() throws InvalidQueryException
	{
		QueryCondition condition = new QueryCondition(NOT, "unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 5, result.size());
	}

	/*
	 * Test with a negated condition that tests for not null on a date field.
	 */
	@Test
	public void testQuery__QuerySpec__05() throws InvalidQueryException
	{
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Only for mSylvestris is gatheringEvent.dateTimeBegin null.
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with a condition that tests for null on number field.
	 */
	@Test
	public void testQuery__QuerySpec__06() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		QueryCondition condition = new QueryCondition(field, EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, so only
		// tRex and mSylvestris (2 specimens) should be returned.
		assertEquals("01", 2, result.size());
	}

	/*
	 * Test with a condition that tests for not null on number field.
	 */
	@Test
	public void testQuery__QuerySpec__07() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		QueryCondition condition = new QueryCondition(field, NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, the other specimens don't.
		assertEquals("01", 3, result.size());
	}

	/*
	 * Test with double negative.
	 */
	@Test
	public void testQuery__QuerySpec__08() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		QueryCondition condition = new QueryCondition(NOT, field, NOT_EQUALS, null);
		// In other words: field EQUALS null
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// pMajor, lFuscus1 and lFuscus2 have site coordinates, so only
		// tRex and mSylvestris (2 specimens) should be returned.
		assertEquals("01", 2, result.size());
	}
}
