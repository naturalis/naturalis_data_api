package nl.naturalis.nba.dao;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Specimen;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.dao.TestSpecimens.*;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;

/**
 * Tests queries with EQUALS operator using the {@link SpecimenDao}.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDao_QueriesWithEqualsOperatorTest {

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
		pMajor = parusMajorSpecimen01();
		lFuscus1 = larusFuscusSpecimen01();
		lFuscus2 = larusFuscusSpecimen02();
		tRex = tRexSpecimen01();
		mSylvestris = malusSylvestrisSpecimen01();
		ESTestUtils.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@After
	public void after()
	{
		// dropIndex(Specimen.class);
	}

	/*
	 * Test with "odd" characters in search term (comma, period).
	 */
	@Test
	public void testQuery__01() throws InvalidQueryException
	{
		// Should yield mSylvestris:
		QueryCondition condition = new QueryCondition("gatheringEvent.localityText", EQUALS,
				"Dorchester, U.K.");
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with "odd" characters in search term and with comparison on field
	 * within or descending from a "nested" object (gatheringPersons).
	 */
	@Test
	public void testQuery__02() throws InvalidQueryException
	{
		// Should yield pMajor, lFuscus1, lFuscus2:
		QueryCondition condition = new QueryCondition("gatheringEvent.gatheringPersons.fullName", EQUALS,
				"Altenburg, R.");
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}

	/*
	 * Test with "odd" characters in search term and with array of nested
	 * objects (gathering persons). Make sure EQUALS also works for 2nd element
	 * of the array.
	 */
	@Test
	public void testQuery__03() throws InvalidQueryException
	{
		// Should yield lFuscus2:
		QueryCondition condition = new QueryCondition("gatheringEvent.gatheringPersons.fullName", EQUALS,
				"Philipp Franz von Siebold");
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test EQUALS for a very long string (the agentText value of Von Siebold)
	 */
	@Test
	public void testQuery__04() throws InvalidQueryException
	{
		// Should yield lFuscus2 (collected by ruudAltenBurg() and vonSiebold()
		QueryCondition condition = new QueryCondition("gatheringEvent.gatheringPersons.agentText", EQUALS,
				vonSiebold().getAgentText());
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

}
