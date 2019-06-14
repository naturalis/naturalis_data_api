package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.SortField;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.mock.SpecimenMock;

public class SpecimenDaoTest_Between {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_Between.class);

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@BeforeClass
	public static void before()
	{
		logger.info("Start");
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

	@After
	public void after()
	{
		// dropIndex(Specimen.class);
	}

	/*
	 * Test with dates.
	 */
	@Test
	public void test__01() throws InvalidQueryException
	{
		OffsetDateTime gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		/*
		 * Construct 2 dates just around gatheringEvent.dateTimeBegin of pMajor.
		 * Each test specimen has a gatheringEvent.dateTimeBegin that lies one
		 * year after the next test specimen, so we can have only one query
		 * result (pMajor).
		 */
		OffsetDateTime from = gatheringDate.minusDays(7L);
		OffsetDateTime to = gatheringDate.plusDays(7L);
		OffsetDateTime[] fromTo = new OffsetDateTime[] { from, to };
		String field = "gatheringEvent.dateTimeBegin";
		QueryCondition condition = new QueryCondition(field, BETWEEN, fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	@Test
	public void test__02() throws InvalidQueryException
	{
		OffsetDateTime gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		OffsetDateTime from = gatheringDate.minusDays(7L);
		OffsetDateTime to = gatheringDate.plusDays(7L);
		OffsetDateTime[] fromTo = new OffsetDateTime[] { from, to };
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", NOT_BETWEEN,
				fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// Since we use NOT_BETWEEN, all specimens except pMajor should come
		// back.
		assertEquals("01", 4, result.size());
	}
	
	/*
	 * Make sure we can use the other accepted date formats ("yyyy-MM-dd")
	 */
	@Test
	public void test__acceptedDateFormat01() throws InvalidQueryException {
		OffsetDateTime gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		/*
		 * Construct 2 dates just around gatheringEvent.dateTimeBegin of pMajor.
		 * Each test specimen has a gatheringEvent.dateTimeBegin that lies one
		 * year after the next test specimen, so we can have only one query
		 * result (pMajor).
		 */
		OffsetDateTime from = gatheringDate.minusDays(7L);
		OffsetDateTime to = gatheringDate.plusDays(7L);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String fromString = from.format(formatter);
		String toString = to.format(formatter);
		String[] fromTo = new String[] { fromString, toString };
		String field = "gatheringEvent.dateTimeBegin";
		QueryCondition condition = new QueryCondition(field, BETWEEN, fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());	
	}
	
	/*
	 * Make sure we can use the other accepted date formats ("yyyy-MM")
	 */
	@Test
	public void test__acceptedDateFormat02() throws InvalidQueryException {
		OffsetDateTime gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		/*
		 * Construct 2 dates just around gatheringEvent.dateTimeBegin of pMajor.
		 * Each test specimen has a gatheringEvent.dateTimeBegin that lies one
		 * year after the next test specimen, so we can have only one query
		 * result (pMajor).
		 */
		OffsetDateTime from = gatheringDate.minusWeeks(8L);
		OffsetDateTime to = gatheringDate.plusWeeks(8L);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
		String fromString = from.format(formatter);
		String toString = to.format(formatter);
		String[] fromTo = new String[] { fromString, toString };
		String field = "gatheringEvent.dateTimeBegin";
		QueryCondition condition = new QueryCondition(field, BETWEEN, fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());	
	}
	

	/*
	 * Test with plain "string dates"
	 */
	@Test
	public void test__03() throws InvalidQueryException
	{
		String from = "2007-04-01";
		String to = "2007-05-01";
		String[] fromTo = new String[] { from, to };
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", BETWEEN,
				fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with plain "string dates"
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		String from = "2007-04-01T00:00:00Z";
		String to = "2007-05-01T23:50:00Z";
		String[] fromTo = new String[] { from, to };
		QueryCondition condition = new QueryCondition("gatheringEvent.dateTimeBegin", BETWEEN,
				fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with field in nested object
	 */
	@Test
	public void testWithNestedObject_01() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.longitudeDecimal";
		double[] longitudes = new double[] { 110.0, 120.0 };
		QueryCondition condition = new QueryCondition(field, BETWEEN, longitudes);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// tRex
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test with field in nested object
	 */
	@Test
	public void testWithNestedObject_02() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.longitudeDecimal";
		double[] longitudes = new double[] { 110.0, 120.0 };
		QueryCondition condition = new QueryCondition(field, NOT_BETWEEN, longitudes);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);

		System.out.println(JsonUtil.toPrettyJson(result));
		
		// All but tRex
		assertEquals("01", 3, result.size());  // This should be 3!!!
		

	}

	/*
	 * Make sure null means: no lower bound
	 */
	@Test
	public void testWithLowerBoundIsNull_01() throws InvalidQueryException
	{
		String field = "numberOfSpecimen";
		Integer[] range = new Integer[] { null, 3 };
		QueryCondition condition = new QueryCondition(field, BETWEEN, range);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// All but lFuscus2
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testWithLowerBoundIsNull_02() throws InvalidQueryException
	{
		String field = "numberOfSpecimen";
		Integer[] range = new Integer[] { null, 2 };
		QueryCondition condition = new QueryCondition(field, NOT_BETWEEN, range);
		List<SortField> sortFields = new ArrayList<>();
		sortFields.add(new SortField("unitID"));
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		qs.setSortFields(sortFields);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// lFuscus2
		assertEquals("01", 3, result.size());
		String expected = lFuscus2.getUnitID();
		String actual = result.iterator().next().getItem().getUnitID();
		assertEquals("02", expected, actual);
	}

	/*
	 * Make sure null means: no upper bound
	 */
	@Test
	public void testWithUpperBoundIsNull_01() throws InvalidQueryException
	{
		String field = "numberOfSpecimen";
		Integer[] range = new Integer[] {2, null };
		QueryCondition condition = new QueryCondition(field, BETWEEN, range);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// lFuscus2
		assertEquals("01", 1, result.size());
		String expected = lFuscus2.getUnitID();
		String actual = result.iterator().next().getItem().getUnitID();
		assertEquals("02", expected, actual);
	}

	@Test
	public void testWithUpperBoundIsNull_02() throws InvalidQueryException
	{
		String field = "numberOfSpecimen";
		Integer[] range = new Integer[] { 3, null };
		QueryCondition condition = new QueryCondition(field, NOT_BETWEEN, range);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		// All but lFuscus2
		assertEquals("01", 4, result.size());
	}
}
