package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
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
import nl.naturalis.nba.dao.mock.SpecimenMock;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_IsNotNull {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_IsNotNull.class);

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

	/*
	 * Test with a condition that tests for NOT NULL on string field.
	 */
	@Test
	public void test__01() throws InvalidQueryException
	{
		QueryCondition condition = new QueryCondition("unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	/*
	 * Test with a negated condition that tests for NOT NULL on string field.
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		QueryCondition condition = new QueryCondition(NOT, "unitGUID", NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 5, result.size());
	}

	/*
	 * Test with a condition that tests for NOT NULL on number field.
	 */
	@Test
	public void test__03() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		QueryCondition condition = new QueryCondition(field, NOT_EQUALS, null);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Only pMajor, lFuscus1, lFuscus2 and tRex have site coordinates.
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test with double negative and nested object
	 */
	@Test
	public void test__08() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.latitudeDecimal";
		QueryCondition condition = new QueryCondition(NOT, field, NOT_EQUALS, null);
		// In other words: field EQUALS null
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// pMajor, lFuscus1, lFuscus2, tRex have site coordinates, so only
		// mSylvestris should be returned.
		assertEquals("01", 1, result.size());
	}
}
