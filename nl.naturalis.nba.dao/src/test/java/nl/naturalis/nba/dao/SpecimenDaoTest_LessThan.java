package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.GT;
import static nl.naturalis.nba.api.ComparisonOperator.GTE;
import static nl.naturalis.nba.api.ComparisonOperator.LT;
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

/*
 * Tests operators <, <=, >, >=
 */
@SuppressWarnings("static-method")
public class SpecimenDaoTest_LessThan {

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

	@Test
	public void test_01() throws InvalidQueryException
	{
		/*
		 * All dateTimeBegin values of the test specimens lie past 2000-01-01,
		 * except mSylvestris which has null for dateTimeBegin. So with LT
		 * (LESS_THAN) we should get back nothing
		 */
		String to = "2000-01-01";
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", LT, to);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
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
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", GT, from);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
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
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", GTE, from);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
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
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", GTE, from);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 3, result.size());
	}
}