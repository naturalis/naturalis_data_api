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
import nl.naturalis.nba.dao.types.ESSpecimen;

@SuppressWarnings("static-method")
public class SpecimenDaoGeoQueryTest {

	static ESSpecimen pMajor;
	static ESSpecimen lFuscus1;
	static ESSpecimen lFuscus2;
	static ESSpecimen tRex;
	static ESSpecimen mSylvestris;

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
		ESTestUtils.saveSpecimens(pMajor, lFuscus1, lFuscus2, tRex, mSylvestris);
	}

	@Test
	public void testQuery_01() throws InvalidQueryException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(52.542892, 4.687768));
		list.add(new LngLatAlt(52.547694, 4.764329));
		list.add(new LngLatAlt(52.501008, 4.765531));
		list.add(new LngLatAlt(52.497299, 4.677957));
		list.add(new LngLatAlt(52.542892, 4.687768));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		Condition condition;
		condition = new Condition("gatheringEvent.siteCoordinates.geoShape", IN, polygon);
		QuerySpec qs = new QuerySpec();
		qs.addCondition(condition);
		SpecimenDao dao = new SpecimenDao();
		QueryResult<Specimen> result = dao.query(qs);
		assertEquals("01", 1, result.size());		
	}

}
