package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.ESTestUtils.queryEquals;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.test.TestPerson;

public class PointInShapeConditionTranslatorTest {

	private static MappingInfo mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo(m);
	}

	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		List<LngLatAlt> list = new ArrayList<>();
		list.add(new LngLatAlt(40, -70));
		list.add(new LngLatAlt(30, -80));
		list.add(new LngLatAlt(20, -90));
		List<List<LngLatAlt>> coords = Arrays.asList(list);
		Polygon polygon = new Polygon();
		polygon.setCoordinates(coords);
		Condition condition = new Condition("address.location", IN, polygon);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		assertEquals("01", PointInShapeConditionTranslator.class, ct.getClass());
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		String file = "PointInShapeConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

}
