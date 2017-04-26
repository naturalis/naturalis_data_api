package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.mock.SpecimenMock.larusFuscusSpecimen01;
import static nl.naturalis.nba.dao.mock.SpecimenMock.larusFuscusSpecimen02;
import static nl.naturalis.nba.dao.mock.SpecimenMock.malusSylvestrisSpecimen01;
import static nl.naturalis.nba.dao.mock.SpecimenMock.parusMajorSpecimen01;
import static nl.naturalis.nba.dao.mock.SpecimenMock.tRexSpecimen01;
import static nl.naturalis.nba.dao.mock.SpecimenMock.vonSiebold;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_Equals {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_Equals.class);

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
		pMajor = parusMajorSpecimen01();
		lFuscus1 = larusFuscusSpecimen01();
		lFuscus2 = larusFuscusSpecimen02();
		tRex = tRexSpecimen01();
		mSylvestris = malusSylvestrisSpecimen01();
		DaoTestUtil.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@After
	public void after()
	{
		// dropIndex(Specimen.class);
	}

	/*
	 * Test with odd characters in search term (comma, period).
	 */
	@Test
	public void test__01() throws InvalidQueryException
	{
		String field = "gatheringEvent.localityText";
		Object value = "Dorchester, U.K.";
		QueryCondition condition = new QueryCondition(field, EQUALS, value);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(query);
		// mSylvestris
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with odd characters in search term and with comparison on field
	 * within or descending from a "nested" object (gatheringPersons).
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		Path field = new Path("gatheringEvent.gatheringPersons.fullName");
		Object value = "Altenburg, R.";
		QueryCondition condition = new QueryCondition(field, EQUALS, value);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(query);
		// pMajor, lFuscus1, lFuscus2
		assertEquals("01", 3, result.size());
	}

	/*
	 * Test with "odd" characters in search term and with array of nested
	 * objects (gathering persons). Make sure EQUALS also works for 2nd element
	 * of the array.
	 */
	@Test
	public void test__03() throws InvalidQueryException
	{
		String field = "gatheringEvent.gatheringPersons.fullName";
		Object value = "Philipp Franz von Siebold";
		QueryCondition condition = new QueryCondition(field, EQUALS, value);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(query);
		// lFuscus2:
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test EQUALS for a very long string (the agentText value of Von Siebold)
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		Path field = new Path("gatheringEvent.gatheringPersons.agentText");
		Object value = vonSiebold().getAgentText();
		QueryCondition condition = new QueryCondition(field, EQUALS, value);
		QuerySpec query = new QuerySpec();
		query.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(query);
		// lFuscus2 (collected by ruudAltenBurg() and vonSiebold()
		assertEquals("01", 1, result.size());
	}
}
