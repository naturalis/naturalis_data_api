package nl.naturalis.nba.dao;

import static nl.naturalis.nba.api.query.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.util.ESUtil.createIndex;
import static nl.naturalis.nba.dao.util.ESUtil.createType;
import static nl.naturalis.nba.dao.util.ESUtil.deleteIndex;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.junit.Before;
import org.junit.Test;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;

@SuppressWarnings("static-method")
public class SpecimenDaoGeoQueryTest {

	static Specimen pMajor;
	static Specimen lFuscus1;
	static Specimen lFuscus2;
	static Specimen tRex;
	static Specimen mSylvestris;

	@Before
	public void before()
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
	}

	@Test
	public void testQuery_01() throws InvalidQueryException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(0.0, 0.0));
		list.add(new LngLatAlt(0.0, 10.0));
		list.add(new LngLatAlt(10.0, 10.0));
		list.add(new LngLatAlt(10.0, 0.0));
		//list.add(new LngLatAlt(0.0, 0.0));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		Condition condition;
		condition = new Condition("gatheringEvent.siteCoordinates.geoPoint", IN, polygon);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		System.out.println(JsonUtil.toPrettyJson(result));
		assertEquals("01", 1, result.size());		
	}

}
