package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.GT;
import static nl.naturalis.nba.api.query.ComparisonOperator.*;
import static nl.naturalis.nba.dao.util.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.ESUtil.createType;
import static nl.naturalis.nba.dao.util.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;

/*
 * Tests the INbaAcess.query with operators <, <=, >, >=
 */
@SuppressWarnings("static-method")
public class SpecimenDao_QueriesWithLessThanOperatorTest {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

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

	@Test
	public void test_01() throws InvalidQueryException
	{
		/*
		 * All dateTimeBegin values of the test specimens lie past 2000-01-01,
		 * except mSylvestris which has null for dateTimeBegin. So with LT
		 * (LESS_THAN) we should get back nothing
		 */
		String to = "2000-01-01";
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", LT, to);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void test_02() throws InvalidQueryException
	{
		/*
		 * All dateTimeBegin values of the test specimens lie past 2000-01-01,
		 * except mSylvestris which has null for dateTimeBegin. So with GT
		 * (GREATER_THAN) we should get back four (all except the one with the
		 * null value).
		 */
		String from = "2000-01-01";
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", GT, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	@Test
	public void test_03() throws InvalidQueryException
	{
		/*
		 * Test with GREATHER THAN OR EQUAL. "2008/04/03 13:04" is the EXACT
		 * dateTimeBegin of lFuscus2, so with GTE operator pMajor, lFuscus1 and
		 * lFuscus2 should come back
		 */
		String from = "2008/04/03 13:04";
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", GTE, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}

	@Test
	public void test_04() throws InvalidQueryException
	{
		/*
		 * Test with GREATHER THAN OR EQUAL. "2008/04/03 13:04" is the EXACT
		 * dateTimeBegin of lFuscus2, so with LTE operator lFuscus2 and 
		 * tRex should come back (mSylvestris
		 */
		String from = "2008/04/03 13:04";
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", GTE, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}
}
