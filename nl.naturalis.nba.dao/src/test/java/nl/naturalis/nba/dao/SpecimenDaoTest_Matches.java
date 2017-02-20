package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_MATCHES;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchResult;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.api.model.Specimen;

/**
 * Tests queries with LIKE operator using the {@link SpecimenDao}.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("static-method")
public class SpecimenDaoTest_Matches {

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
	 * Test with null (should get error).
	 */
	@Test
	public void test__01()
	{
		String expecting = "Search value must not be null when using operator MATCHES";
		String name = "identifications.systemClassification.name";
		SearchCondition condition = new SearchCondition(name, MATCHES, null);
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		try {
			dao.search(qs);
			fail("Expected an InvalidQueryException");
		}
		catch (InvalidQueryException e) {
			assertTrue(e.getMessage().contains(expecting));
		}
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__02() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition(rank, MATCHES, "bossen"));
		/*
		 * That's larusFuscusSpecimen01 : "bossen" MATCHES
		 * "In de bossen nabij Aalten"
		 */
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 1, result.size());
		String unitID = result.get(0).getItem().getUnitID();
		assertEquals("02", lFuscus1.getUnitID(), unitID);
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void testQuery__03() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition(rank, NOT_MATCHES, "bossen"));
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 4, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__04() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition(rank, MATCHES, "   bossen  one two three"));
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 1, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__05() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition(rank, MATCHES, "   nabij"));
		/*
		 * That's larusFuscusSpecimen01 &parusMajorSpecimen01
		 */
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 2, result.size());
	}

	/*
	 * Test happy flow
	 */
	@Test
	public void test__06() throws InvalidQueryException
	{
		String rank = "gatheringEvent.localityText";
		SearchSpec qs = new SearchSpec();
		qs.addCondition(new SearchCondition(rank, NOT_MATCHES, "   nabij"));
		/*
		 * That's all but larusFuscusSpecimen01 &parusMajorSpecimen01
		 */
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 3, result.size());
	}
}
