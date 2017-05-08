package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.DaoTestUtil.saveGeoAreas;
import static nl.naturalis.nba.dao.DaoTestUtil.saveSpecimens;
import static nl.naturalis.nba.dao.DocumentType.GEO_AREA;
import static nl.naturalis.nba.dao.DocumentType.SPECIMEN;
import static nl.naturalis.nba.dao.mock.GeoAreaMock.Aalten;
import static nl.naturalis.nba.dao.mock.GeoAreaMock.Netherlands;
import static nl.naturalis.nba.dao.mock.GeoAreaMock.NoordHolland;
import static nl.naturalis.nba.dao.mock.GeoAreaMock.Uitgeest;
import static nl.naturalis.nba.dao.mock.GeoAreaMock.Vatican;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndices;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndices;
import static nl.naturalis.nba.dao.util.es.ESUtil.getDistinctIndices;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.mock.SpecimenMock;

@SuppressWarnings("static-method")
public class SpecimenDaoTest_GeoQueries {

	private static final Logger logger = DaoRegistry.getInstance()
			.getLogger(SpecimenDaoTest_GeoQueries.class);

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	static GeoArea aalten;
	static GeoArea uitgeest;
	static GeoArea noordHolland;
	static GeoArea netherlands;
	static GeoArea vatican;

	@BeforeClass
	public static void before()
	{
		logger.info("Starting tests");

		Set<IndexInfo> indices = getDistinctIndices(SPECIMEN, GEO_AREA);
		deleteIndices(indices);
		createIndices(indices);

		/*
		 * Insert 5 test specimens.
		 */
		pMajor = SpecimenMock.parusMajorSpecimen01();
		lFuscus1 = SpecimenMock.larusFuscusSpecimen01();
		lFuscus2 = SpecimenMock.larusFuscusSpecimen02();
		tRex = SpecimenMock.tRexSpecimen01();
		mSylvestris = SpecimenMock.malusSylvestrisSpecimen01();
		saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);

		/*
		 * Insert 4 test geo areas.
		 */
		aalten = Aalten();
		uitgeest = Uitgeest();
		noordHolland = NoordHolland();
		netherlands = Netherlands();
		vatican = Vatican();

		saveGeoAreas(aalten, uitgeest, noordHolland, netherlands);
	}

	@Test
	public void testQuery_01a() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(field, IN, aalten.getShape());
		// That's lFuscus1
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	@Test
	public void testQuery_01b() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(field, IN, uitgeest.getShape());
		// That's pMajor and lFuscus2
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_01c() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(field, IN, noordHolland.getShape());
		// That's pMajor and lFuscus2
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_02a() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(field, IN, "Aalten");
		// That's lFuscus1
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());
	}

	@Test
	public void testQuery_02b() throws InvalidQueryException
	{
		String field = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(field, IN, "Uitgeest");
		// That's pMajor and lFuscus2
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 2, result.size());
	}

	@Test
	public void testQuery_02c() throws InvalidQueryException
	{
		String site = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(site, IN, "Netherlands");
		// That's pMajor and lFuscus2
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}

	@Test
	public void testQuery_03c() throws InvalidQueryException
	{
		String site = "gatheringEvent.siteCoordinates.geoShape";
		QueryCondition condition = new QueryCondition(site, IN, vatican.getShape());
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		// System.out.println(JsonUtil.toPrettyJson(qs));
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 0, result.size());
	}

	@Test
	public void testQuery_04() throws InvalidQueryException
	{
		QueryCondition qc = new QueryCondition();
		qc.setField(new Path("gatheringEvent.siteCoordinates.geoShape"));
		qc.setOperator(ComparisonOperator.IN);
		MultiPolygon polygon = new MultiPolygon();
		
		LngLatAlt coord00 = new LngLatAlt(60, -60);
		LngLatAlt coord01 = new LngLatAlt(60, 60);
		LngLatAlt coord02 = new LngLatAlt(-60, 60);
		LngLatAlt coord03 = new LngLatAlt(-60, -60);
		List<LngLatAlt> ring0 = Arrays.asList(coord00, coord01, coord02, coord03, coord00);

		LngLatAlt coord10 = new LngLatAlt(40, -40);
		LngLatAlt coord11 = new LngLatAlt(40, 40);
		LngLatAlt coord12 = new LngLatAlt(-40, 40);
		LngLatAlt coord13 = new LngLatAlt(-40, -40);
		List<LngLatAlt> ring1 = Arrays.asList(coord10, coord11, coord12, coord13, coord10);

		List<List<LngLatAlt>> rings = Arrays.asList(ring0, ring1);
		
		List<List<List<LngLatAlt>>> coords = Arrays.asList(rings);

		polygon.setCoordinates(coords);
		qc.setValue(polygon);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(qc);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 3, result.size());
	}

}
