package nl.naturalis.nba.dao.es;

import static nl.naturalis.nba.api.query.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.dao.es.ESTestUtils.createIndex;
import static nl.naturalis.nba.dao.es.ESTestUtils.createType;
import static nl.naturalis.nba.dao.es.ESTestUtils.dropIndex;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.types.ESSpecimen;

public class SpecimenDAOWithBetweenConditionTest {

	static ESSpecimen pMajor;
	static ESSpecimen lFuscus1;
	static ESSpecimen lFuscus2;
	static ESSpecimen tRex;
	static ESSpecimen mSylvestris;

	@Before
	public void before()
	{
		dropIndex(DocumentType.SPECIMEN);
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

	@Test
	public void testQuery__QuerySpec__01() throws InvalidQueryException
	{
		Date gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		Instant instant = gatheringDate.toInstant();
		ZoneId dfault = ZoneId.systemDefault();
		OffsetDateTime two = OffsetDateTime.ofInstant(instant, dfault).minusDays(7L);
		OffsetDateTime t = OffsetDateTime.ofInstant(instant, dfault).plusDays(7L);
		OffsetDateTime[] fromTo = new OffsetDateTime[] { two, t };
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", BETWEEN, fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDAO dao = new SpecimenDAO();
		Specimen[] result = dao.query(qs);
		// Each test specimen has a gatheringEvent.dateTimeBegin that lies one
		// year after the next test specimen, so we can have only one query
		// result (pMajor).
		assertEquals("01", 1, result.length);
	}

	@Test
	public void testQuery__QuerySpec__02() throws InvalidQueryException
	{
		Date gatheringDate = pMajor.getGatheringEvent().getDateTimeBegin();
		Instant instant = gatheringDate.toInstant();
		ZoneId dfault = ZoneId.systemDefault();
		OffsetDateTime from = OffsetDateTime.ofInstant(instant, dfault).minusDays(7L);
		OffsetDateTime to = OffsetDateTime.ofInstant(instant, dfault).plusDays(7L);
		OffsetDateTime[] fromTo = new OffsetDateTime[] { from, to };
		Condition condition = new Condition("gatheringEvent.dateTimeBegin", NOT_BETWEEN, fromTo);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDAO dao = new SpecimenDAO();
		Specimen[] result = dao.query(qs);
		// Since we use NOT_BETWEEN, all specimens except pMajor should come
		// back.
		assertEquals("01", 4, result.length);
	}

}
