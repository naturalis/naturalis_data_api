package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.GT;
import static nl.naturalis.nba.api.ComparisonOperator.GTE;
import static nl.naturalis.nba.api.ComparisonOperator.LT;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.mock.SpecimenMock;

/*
 * Tests operators <, <=, >, >=
 */
@SuppressWarnings("static-method")
public class SpecimenDaoTest_LessThan {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_LessThan.class);

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

	@Test
	public void test_01a() throws InvalidQueryException
	{
		/*
		 * All dateTimeBegin values of the test specimens lie past 2000-01-01,
		 * except mSylvestris which has null for dateTimeBegin. So with LT
		 * (LESS_THAN) we should get back nothing
		 */
		String to = "2000-01-01";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", LT, to);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void test_01b() throws InvalidQueryException
	{
		/*
		 * Just test with some other of the allowed date formats
		 */
		String to = "2000-01";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", LT, to);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void test_01c() throws InvalidQueryException
	{
		/*
		 * Just test with some other of the allowed date formats
		 */
		String to = "2000";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", LT, to);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void test_01d() throws InvalidQueryException
	{
		/*
		 * Just test with some other of the allowed date formats
		 */
		String to = "2000-01-01 00:00:00";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", LT, to);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void test_01e() throws InvalidQueryException
	{
		/*
		 * Just test with some other of the allowed date formats
		 */
		String to = "2000-01-01T00:00:00+02:00";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", LT, to);
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
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GT, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	@Test
	public void test_02b() throws InvalidQueryException
	{
		String from = "2000-01";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GT, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	@Test
	public void test_02c() throws InvalidQueryException
	{
		String from = "2000";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GT, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	@Test
	public void test_02d() throws InvalidQueryException
	{
		String from = "2000-01-01 00:00:00";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GT, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	@Test
	public void test_02e() throws InvalidQueryException
	{
		String from = "2000-01-01T00:00:00+02:00";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GT, from);
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
		 * Test with GREATHER THAN OR EQUAL. "2008-04-03 13:04" is the EXACT
		 * dateTimeBegin of lFuscus2, so with GTE operator pMajor, lFuscus1 and
		 * lFuscus2 should come back
		 */
		String from = "2008-04-03 13:04";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GTE, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		System.out.println(JsonUtil.toPrettyJson(qs));
		assertEquals("01", 3, result.size());
	}

	@Test
	public void test_04() throws InvalidQueryException
	{
		/*
		 * Test with GREATHER THAN OR EQUAL. "2008-04-03 13:04" is the EXACT
		 * dateTimeBegin of lFuscus2, so with LTE operator lFuscus2 and tRex
		 * should come back (mSylvestris
		 */
		String from = "2008-04-03 13:04";
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", GTE, from);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}
}
