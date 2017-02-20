package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.ComparisonOperator.IN;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.test.TestPerson;


@SuppressWarnings("static-method")
public class InConditionTranslatorTest {

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
		SearchCondition condition = new SearchCondition("firstName", IN, null);	
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
		SearchCondition condition = new SearchCondition("firstName", IN, new Integer[] { null, null, null });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/InConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with Condition.value is an array with only non-null values
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		SearchCondition condition = new SearchCondition("pets.name", IN, new String[] { "Napoleon", "Max" });		
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		String file = "translate/search/InConditionTranslatorTest__testTranslate_02.json";
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
		SearchCondition condition = new SearchCondition("pets.name", IN, values);	
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		String file = "translate/search/InConditionTranslatorTest__testTranslate_03.json";
		assertTrue("01", queryEquals(query, file));
	}

}
