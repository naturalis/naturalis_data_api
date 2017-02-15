package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.test.TestPerson;
import nl.naturalis.nba.dao.translate.query.ConditionTranslator;


@SuppressWarnings("static-method")
public class InValuesConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<TestPerson> m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(m);
	}

	/*
	 * Test with Condition.value is null (should cause an error)
	 */
	@Test(expected=InvalidConditionException.class)
	public void testTranslate_01a() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", IN, null);	
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
		// System.out.println(query);
	}

	/*
	 * Test with Condition.value is an array with only null values.
	 */
	@Test
	public void testTranslate_01b() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", IN, new Integer[] { null, null, null });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/query/InConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with Condition.value is an array with only non-null values
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("pets.name", IN, new String[] { "Napoleon", "Max" });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		String file = "translate/query/InConditionTranslatorTest__testTranslate_02.json";
		assertTrue("01", queryEquals(query, file));
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
		QueryCondition condition = new QueryCondition("pets.name", IN, values);	
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		System.out.println(query);
		String file = "translate/query/InConditionTranslatorTest__testTranslate_03.json";
		assertTrue("01", queryEquals(query, file));
	}

}
