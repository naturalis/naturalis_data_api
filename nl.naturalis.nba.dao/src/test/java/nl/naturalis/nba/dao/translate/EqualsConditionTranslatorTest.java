package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.translate.ConditionTranslator;
import nl.naturalis.nba.dao.translate.IsNotNullConditionTranslator;

@SuppressWarnings("static-method")
public class EqualsConditionTranslatorTest {

	private static MappingInfo<EqualsTestObject> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<EqualsTestObject> m = MappingFactory.getMapping(EqualsTestObject.class);
		mappingInfo = new MappingInfo<>(m);
	}

	/*
	 * Tests that comparing field with null using EQUALS operator results in
	 * ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator results in
	 * a doubly negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", NOT_EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		assertEquals("01", ct.getClass(), IsNotNullConditionTranslator.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertEquals("02", query.getClass(), ExistsQueryBuilder.class);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_02.json";
		assertTrue("03", queryEquals(query, file));
	}

	/*
	 * Tests that comparing field with null using EQUALS operator and NOT
	 * operator results in a doubly-negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_03() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition(NOT, "firstName", EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_03.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator and NOT
	 * operator results in a triply-negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_04() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition(NOT, "firstName", NOT_EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		assertEquals("01", ct.getClass(), IsNotNullConditionTranslator.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("02", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_04.json";
		assertTrue("03", queryEquals(query, file));
	}

	/*
	 * Tests that a simple condition (without siblings and without the NOT
	 * operator) gets translated into a term query.
	 */
	@Test
	public void testTranslate_05() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, "Smith");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof TermQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_05.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test translation with a Condition with one AND sibling.
	 */
	@Test
	public void testTranslate_06() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_06.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test translation with a NOT_EQUALS Condition with one AND sibling.
	 */
	@Test
	public void testTranslate_07() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", NOT_EQUALS, "John");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_07.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with 3 AND siblings.
	 */
	@Test
	public void testTranslate_08() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		condition.and("married", NOT_EQUALS, true);
		condition.and("favouritePet", NOT_EQUALS, "dog");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_08.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Tests that NOT operator negates not just the condition itself but the
	 * combination of condition and its AND and OR siblings.
	 */
	@Test
	public void testTranslate_09() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition(NOT, "firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		condition.and("age", EQUALS, 40);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_09.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with deeply nested conditions.
	 */
	@Test
	public void testTranslate_10() throws InvalidConditionException
	{
		QueryCondition isJohnSmith = new QueryCondition("firstName", EQUALS, "John");
		isJohnSmith.and("lastName", EQUALS, "Smith");

		QueryCondition inNetherlands = new QueryCondition("country", EQUALS, "Netherlands");
		inNetherlands
				.and(new QueryCondition("city", EQUALS, "Amsterdam").or("city", EQUALS, "Rotterdam"));

		QueryCondition inGermany = new QueryCondition("country", EQUALS, "Germany");
		inGermany.and(new QueryCondition("city", EQUALS, "Berlin").or("city", EQUALS, "Hanover"));

		isJohnSmith.and(inNetherlands.or(inGermany));

		ConditionTranslator ct = getTranslator(isJohnSmith, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_10.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test with 2 OR siblings.
	 */
	@Test
	public void testTranslate_11() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, "John");
		condition.or("firstName", EQUALS, "Peter").or("firstName", EQUALS, "Mark");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_11.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Tests operator precedence (AND binds stronger than OR).
	 */
	@Test
	public void testTranslate_12() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith").and("favouriteFood", EQUALS, "Chinese");
		condition.or("city", EQUALS, "Amsterdam").or("city", EQUALS, "Rotterdam").or("city", EQUALS,
				"Leiden");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testTranslate_12.json";
		assertTrue("02", queryEquals(query, file));
	}

}
