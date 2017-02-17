package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.DaoTestUtil.saveGeoAreas;
import static nl.naturalis.nba.dao.DaoTestUtil.saveSpecimens;
import static nl.naturalis.nba.dao.TestGeoAreas.Aalten;
import static nl.naturalis.nba.dao.TestGeoAreas.*;
import static nl.naturalis.nba.dao.TestGeoAreas.Uitgeest;
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
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.Specimen;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_GeoQueries {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	static GeoArea aalten;
	static GeoArea uitgeest;
	static GeoArea noordHolland;
	static GeoArea netherlands;

	@BeforeClass
	public static void before()
	{
		deleteIndex(SPECIMEN);
		createIndex(SPECIMEN);
		createType(SPECIMEN);

		deleteIndex(GEO_AREA);
		createIndex(GEO_AREA);
		createType(GEO_AREA);

		/*
		 * Insert 5 test specimens.
		 */
		pMajor = TestSpecimens.parusMajorSpecimen01();
		lFuscus1 = TestSpecimens.larusFuscusSpecimen01();
		lFuscus2 = TestSpecimens.larusFuscusSpecimen02();
		tRex = TestSpecimens.tRexSpecimen01();
		mSylvestris = TestSpecimens.malusSylvestrisSpecimen01();
		saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);

		/*
		 * Insert 4 test geo areas.
		 */
		aalten = Aalten();
		uitgeest = Uitgeest();
		noordHolland = NoordHolland();
		netherlands = Netherlands();
		saveGeoAreas(aalten, uitgeest, noordHolland, netherlands);
	}

	@Test
	public void testQuery_01a() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(field, IN, aalten.getShape());
		// That's lFuscus1
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 1, result.size());
	}

	@Test
	public void testQuery_01b() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(field, IN, uitgeest.getShape());
		// That's pMajor and lFuscus2
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_01c() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(field, IN, noordHolland.getShape());
		// That's pMajor and lFuscus2
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_02a() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(field, IN, "Aalten");
		// That's lFuscus1
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 1, result.size());
	}

	@Test
	public void testQuery_02b() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(field, IN, "Uitgeest");
		// That's pMajor and lFuscus2
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_02c() throws InvalidQueryException
	{
		String site = "gatheringEvent.siteCoordinates.geoShape";
		SearchCondition condition = new SearchCondition(site, IN, "Netherlands");
		// That's pMajor and lFuscus2
		SearchSpec qs = new SearchSpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		SearchResult<Specimen> result = dao.search(qs);
		assertEquals("01", 3, result.size());
	}

}
