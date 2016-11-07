package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static nl.naturalis.nba.api.query.UnaryBooleanOperator.NOT;
import static nl.naturalis.nba.dao.ESTestUtils.queryEquals;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;

public class EqualsConditionTranslatorTest {

	private static MappingInfo mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<EqualsTestObject> m = MappingFactory.getMapping(EqualsTestObject.class);
		mappingInfo = new MappingInfo(m);
	}

	/*
	 * Tests that comparing field with null using EQUALS operator results in
	 * ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator results in
	 * a doubly negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", NOT_EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_02.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests that comparing field with null using EQUALS operator and NOT
	 * operator results in a doubly-negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_03() throws InvalidConditionException
	{
		Condition condition = new Condition(NOT, "firstName", EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_03.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator and NOT
	 * operator results in a triply-negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_04() throws InvalidConditionException
	{
		Condition condition = new Condition(NOT, "firstName", NOT_EQUALS, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_04.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests that a simple condition (without siblings and without the NOT
	 * operator) gets translated into a term query.
	 */
	@Test
	public void testTranslate_05() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "Smith");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof TermQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_05.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Test translation with a Condition with one AND sibling.
	 */
	@Test
	public void testTranslate_06() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_06.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Test translation with a NOT_EQUALS Condition with one AND sibling.
	 */
	@Test
	public void testTranslate_07() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", NOT_EQUALS, "John");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_07.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Test with 3 AND siblings.
	 */
	@Test
	public void testTranslate_08() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		condition.and("married", NOT_EQUALS, true);
		condition.and("favouritePet", NOT_EQUALS, "dog");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_08.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests that NOT operator negates not just the condition itself but the
	 * combination of condition and its AND and OR siblings.
	 */
	@Test
	public void testTranslate_09() throws InvalidConditionException
	{
		Condition condition = new Condition(NOT, "firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		condition.and("age", EQUALS, 40);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_09.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Test with deeply nested conditions.
	 */
	@Test
	public void testTranslate_10() throws InvalidConditionException
	{
		Condition isJohnSmith = new Condition("firstName", "=", "John");
		isJohnSmith.and("lastName", "=", "Smith");

		Condition inNetherlands = new Condition("country", "=", "Netherlands");
		inNetherlands.and(new Condition("city", "=", "Amsterdam").or("city", "=", "Rotterdam"));

		Condition inGermany = new Condition("country", "=", "Germany");
		inGermany.and(new Condition("city", "=", "Berlin").or("city", "=", "Hanover"));

		isJohnSmith.and(inNetherlands.or(inGermany));

		ConditionTranslator ct = getTranslator(isJohnSmith, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_10.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Test with 2 OR siblings.
	 */
	@Test
	public void testTranslate_11() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", "=", "John");
		condition.or("firstName", "=", "Peter").or("firstName", "=", "Mark");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_11.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	/*
	 * Tests operator precedence (AND binds stronger than OR).
	 */
	@Test
	public void testTranslate_12() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", "=", "John");
		condition.and("lastName", "=", "Smith").and("favouriteFood", "=", "Chinese");
		condition.or("city", "=", "Amsterdam").or("city", "=", "Rotterdam").or("city", "=",
				"Leiden");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_12.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

}
