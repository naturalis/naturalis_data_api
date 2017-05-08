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

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.geojson.LngLatAlt;
import org.geojson.MultiPolygon;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.mock.SpecimenMock;
import nl.naturalis.nba.dao.translate.ConditionTranslator;
import nl.naturalis.nba.dao.translate.ConditionTranslatorFactory;

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

//	@Test
//	public void testQuery_04() throws InvalidQueryException
//	{
//		QueryCondition qc = new QueryCondition();
//		qc.setField(new Path("gatheringEvent.siteCoordinates.geoShape"));
//		qc.setOperator(ComparisonOperator.IN);
//		MultiPolygon polygon = new MultiPolygon();
//		TypeReference<List<List<List<LngLatAlt>>>> typeRef = new TypeReference<List<List<List<LngLatAlt>>>>() {};
//		String s = "[ [ [ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ], [ [100.35, 0.35], [100.65, 0.35], [100.65, 0.65], [100.35, 0.65], [100.35, 0.35] ] ] ]";
//		List<List<List<LngLatAlt>>> coords = JsonUtil.deserialize(s, typeRef);
//		polygon.setCoordinates(coords);
//		qc.setValue(polygon);
//		QuerySpec qs = new QuerySpec();
//		qs.addCondition(qc);
//		SpecimenDao dao = new SpecimenDao();
//		QueryResult<Specimen> result = dao.query(qs);
//		assertEquals("01", 0, result.size());
//	}

}
