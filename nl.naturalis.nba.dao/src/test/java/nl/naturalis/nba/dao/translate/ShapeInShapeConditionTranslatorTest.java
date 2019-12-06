package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.*;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.mock.SpecimenMock;
import nl.naturalis.nba.dao.test.TestPerson;
import nl.naturalis.nba.dao.translate.ConditionTranslator;
import nl.naturalis.nba.dao.translate.ShapeInShapeConditionTranslator;

public class ShapeInShapeConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<TestPerson> m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(m);
	}

	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(40, -70));
		list.add(new LngLatAlt(30, -80));
		list.add(new LngLatAlt(20, -90));
		list.add(new LngLatAlt(40, -70));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		
		
		QueryCondition condition = new QueryCondition("address.locationAsShape", IN, polygon);
		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		assertEquals("01", ShapeInShapeConditionTranslator.class, ct.getClass());
		// System.out.println("> \n" + JsonUtil.toPrettyJson(ct));
		
		QueryBuilder query = ct.translate();
		System.out.println("unit test:\n" +query);

		String file = "translate/search/ShapeInShapeConditionTranslatorTest__testTranslate_01.json";
		
		
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with NOT_IN
	 */
	@Ignore
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(40, -70));
		list.add(new LngLatAlt(30, -80));
		list.add(new LngLatAlt(20, -90));
		list.add(new LngLatAlt(40, -70));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		QueryCondition condition = new QueryCondition("address.locationAsShape", NOT_IN, polygon);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		assertEquals("01", ShapeInShapeConditionTranslator.class, ct.getClass());
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		String file = "translate/search/ShapeInShapeConditionTranslatorTest__testTranslate_02.json";
		assertTrue("02", queryEquals(query, file));
	}
}
