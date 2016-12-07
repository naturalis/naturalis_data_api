package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.TestGeoAreas.Aalten;
import static nl.naturalis.nba.dao.util.es.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.es.ESUtil.createType;
import static nl.naturalis.nba.dao.util.es.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

@SuppressWarnings("static-method")
public class SpecimenDao_GeoQueriesTest {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	static GeoArea aalten;

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
		ESTestUtils.saveSpecimens(pMajor);

		aalten = Aalten();

		ESTestUtils.saveGeoArea(aalten, true);

		createType(DocumentType.GEO_AREA);
	}

	@Test
	public void testQuery_02() throws InvalidQueryException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(0.0, 0.0));
		list.add(new LngLatAlt(0.0, 10.0));
		list.add(new LngLatAlt(10.0, 10.0));
		list.add(new LngLatAlt(10.0, 0.0));
		list.add(new LngLatAlt(0.0, 0.0));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		Condition condition;
		condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, polygon);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		System.out.println(JsonUtil.toPrettyJson(result));
		assertEquals("01", 1, result.size());
	}

	//@Test
	/*
	 * TODO: Test with pre-indexed shape. This does currently not work because:
	 * 
	 * 
	 * [1] There are no documents in GeoArea in nba_integration_test index. Must
	 * load one or two shapes (e.g. one for "Netherlands"). For example in
	 * 
	 * @Before
	 * 
	 * [2] Make sure the siteCoordinates for the specimens lie within the loaded
	 * shapes.
	 */
	public void testQuery_03() throws InvalidQueryException
	{
		Condition condition;
		condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, "1004050@GEO");
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		System.out.println(JsonUtil.toPrettyJson(result));
		assertEquals("01", 1, result.size());
	}

}
