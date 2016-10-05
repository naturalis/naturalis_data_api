package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.domainobject.util.FileUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.query.ConditionTranslator;
import nl.naturalis.nba.dao.test.TestPerson;


public class InConditionTranslatorTest {

	private static MappingInfo mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo(m);
	}

	/*
	 * Test with Condition.value is null.
	 */
	@Test
	public void testTranslate_01a() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", IN, null);	
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "InConditionTranslatorTest__testTranslate_01.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Test with Condition.value is an array with only null values.
	 */
	@Test
	public void testTranslate_01b() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", IN, new Integer[] { null, null, null });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "InConditionTranslatorTest__testTranslate_01.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Test with Condition.value is an array with only non-null values
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		Condition condition = new Condition("pets.name", IN, new String[] { "Napoleon", "Max" });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		String file = "InConditionTranslatorTest__testTranslate_02.json";
		assertEquals("01", getContents(file), query.toString());
	}

	/*
	 * Test with Condition.value is an array with some null and some non-null
	 * values
	 */
	@Test
	public void testTranslate_03() throws InvalidConditionException
	{
		List<String> values = new ArrayList<>(3);
		values.add("Napoleon");
		values.add("Max");
		values.add(null);
		Condition condition = new Condition("pets.name", IN, values);	
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		String file = "InConditionTranslatorTest__testTranslate_03.json";
		assertEquals("01", getContents(file), query.toString());
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
