package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_IN;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchResult;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.model.Specimen;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_In {

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

	@After
	public void after()
	{
		// dropIndex(Specimen.class);
	}

	/*
	 * Test where one of the search values matches a document
	 */
	@Test
	public void testQuery__SearchSpec__01() throws InvalidQueryException
	{
		Date gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		Instant instant = gatheringDate.toInstant();
		ZoneId dfault = ZoneId.systemDefault();
		OffsetDateTime date1 = OffsetDateTime.ofInstant(instant, dfault);
		OffsetDateTime date2 = OffsetDateTime.ofInstant(instant, dfault).minusDays(7L);
		OffsetDateTime date3 = OffsetDateTime.ofInstant(instant, dfault).plusDays(7L);
		OffsetDateTime[] options = new OffsetDateTime[] { date1, date2, date3 };
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", IN,
				options);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
		String unitID = result.get(0).getItem().getUnitID();
		assertEquals("02", pMajor.getUnitID(), unitID);
	}

	/*
	 * Test where one of the search values matches a document (negated).
	 */
	@Test
	public void testQuery__SearchSpec__02() throws InvalidQueryException
	{
		Date gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		Instant instant = gatheringDate.toInstant();
		ZoneId dfault = ZoneId.systemDefault();
		OffsetDateTime date1 = OffsetDateTime.ofInstant(instant, dfault);
		OffsetDateTime date2 = OffsetDateTime.ofInstant(instant, dfault).minusDays(7L);
		OffsetDateTime date3 = OffsetDateTime.ofInstant(instant, dfault).plusDays(7L);
		OffsetDateTime[] options = new OffsetDateTime[] { date1, date2, date3 };
		SearchCondition condition = new SearchCondition("gatheringEvent.dateTimeBegin", NOT_IN,
				options);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test where one of the search values matches a document (negated).
	 */
	@Test
	public void testQuery__SearchSpec__03() throws InvalidQueryException
	{
		Date gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		Instant instant = gatheringDate.toInstant();
		ZoneId dfault = ZoneId.systemDefault();
		OffsetDateTime date1 = OffsetDateTime.ofInstant(instant, dfault);
		OffsetDateTime date2 = OffsetDateTime.ofInstant(instant, dfault).minusDays(7L);
		OffsetDateTime date3 = OffsetDateTime.ofInstant(instant, dfault).plusDays(7L);
		OffsetDateTime[] options = new OffsetDateTime[] { date1, date2, date3 };
		SearchCondition condition = new SearchCondition(NOT, "gatheringEvent.dateTimeBegin", IN,
				options);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test where one of the search values is null (so the searched field must
	 * be either null/empty or have one of the non-null search values)
	 */
	@Test
	public void testQuery__SearchSpec__04() throws InvalidQueryException
	{
		String field = "gatheringEvent.gatheringPersons.agentText";
		String[] options = new String[] { "Also likes David Bowie", null };
		/*
		 * This selects specimens with collector Ruud Altenburg or Nathaniel
		 * Wallich, but excludes Edwin van Huis, so 4 specimens should be
		 * returned.
		 */
		SearchCondition condition = new SearchCondition(field, IN, options);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.query(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test where one of the search values is null in combination with NOT_IN.
	 * So the searched field must be neither be null/empty nor have ANY of the
	 * other (non-null) search values.
	 */
	@Test
	public void testQuery__SearchSpec__05() throws InvalidQueryException
	{
		String field = "gatheringEvent.gatheringPersons.agentText";
		String[] options = new String[] { "Also likes David Bowie", null };
		SearchCondition condition = new SearchCondition(field, NOT_IN, options);
		SearchSpec qs = new SearchSpec();
		qs.setConstantScore(true);
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

}
