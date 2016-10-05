package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.util.ESUtil.*;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.dao.types.ESSpecimen;

/**
 * Tests the SpecimenDAO class with conditions that either test for null or not
 * null.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDaoNullChecksTest {

	static ESSpecimen pMajor;
	static ESSpecimen lFuscus1;
	static ESSpecimen lFuscus2;
	static ESSpecimen tRex;
	static ESSpecimen mSylvestris;

	@Before
	public void before()
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
		// dropIndex(ESSpecimen.class);
	}

	/*
	 * Test with condition that tests for null.
	 */
	@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		// UnitGUID is null in all test specimens, so query should return them
		// all.
		Condition condition = new Condition("unitGUID", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 5, result.length);
	}

	/*
	 * Test with a negated condition that tests for null.
	 */
	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		// UnitGUID is always null, so query should return 0 specimens.
		Condition condition = new Condition(NOT, "unitGUID", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 0, result.length);
	}

	/*
	 * Test with a condition that tests for not null.
	 */
	@Test
	public void testQuery__QuerySpec__03() throws InvalidQueryException
	{
		Condition condition = new Condition("unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 0, result.length);
	}

	/*
	 * Test with a negated condition that tests for not null.
	 */
	@Test
	public void testQuery__QuerySpec__04() throws InvalidQueryException
	{
		Condition condition = new Condition(NOT, "unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		assertEquals("01", 5, result.length);
	}

	/*
	 * Test with a negated condition that tests for not null on a date field.
	 */
	@Test
	public void testQuery__QuerySpec__05() throws InvalidQueryException
	{
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		Specimen[] result = dao.query(qs);
		// Only for mSylvestris is gatheringEvent.dateTimeBegin null.
		assertEquals("01", 1, result.length);
	}

}
