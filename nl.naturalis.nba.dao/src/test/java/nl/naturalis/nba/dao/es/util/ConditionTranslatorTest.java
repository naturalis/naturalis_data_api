package nl.naturalis.nba.dao.es.util;

import static nl.naturalis.nba.api.query.Operator.EQUALS;
import static nl.naturalis.nba.api.query.Operator.NOT_EQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.domainobject.util.FileUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;

public class ConditionTranslatorTest {

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the EQUALS operator
	 * <li>has no siblings
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition condition = new Condition("name", EQUALS, "Smith");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof TermQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_01.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the EQUALS operator
	 * <li>has one AND sibling also using the EQUALS operator
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_02.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the NOT_EQUALS operator
	 * <li>has no siblings
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_03() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", NOT_EQUALS, "John");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_03.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the EQUALS operator
	 * <li>has three AND siblings, one using the EQUALS operator and two using
	 * the NOT_EQUALS operator.
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_04() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.and("lastName", EQUALS, "Smith");
		condition.and("hasChildren", NOT_EQUALS, "true");
		condition.and("favoritePet", NOT_EQUALS, "dog");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_04.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the EQUALS operator
	 * <li>has one OR sibling also using the EQUALS operator
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_05() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.or("lastName", EQUALS, "Smith");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_05.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests the translate() method for a Condition that
	 * <ul>
	 * <li>uses the NOT_EQUALS operator
	 * <li>has two OR sibling using the NOT_EQUALS and EQUALS operator,
	 * respectively.
	 * </ul>
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_06() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", NOT_EQUALS, "John");
		condition.or("lastName", NOT_EQUALS, "Smith");
		condition.or("favoritePet", EQUALS, "dog");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_06.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	/**
	 * Tests deep nesting of conditions.
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_07() throws InvalidConditionException
	{

		Condition deepest = new Condition("pet", EQUALS, "parrot");
		deepest.and("color", EQUALS, "blue").and("talks", EQUALS, "true");

		Condition deeper = new Condition("pet", EQUALS, "dog");
		deeper.or("pet", EQUALS, "cat").or(deepest);

		Condition condition = new Condition("firstName", EQUALS, "John");
		condition.and("lastName", NOT_EQUALS, "Smith").and(deeper);

		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "ConditionTranslatorTest__testTranslate_07.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}

	@Test(expected = InvalidConditionException.class)
	public void testTranslate_08() throws InvalidConditionException
	{
		Condition condition = new Condition("pet", EQUALS, "parrot");
		condition.and("color", EQUALS, "blue");
		condition.or("talks", EQUALS, "true");
		/*
		 * NB providing both AND and OR siblings in itself does not provoke the
		 * exception; only when the condition is translated do you get the
		 * exception
		 */
		new ConditionTranslator(condition).translate();
	}

	/**
	 * Test with nested queries.
	 * 
	 * @throws InvalidConditionException
	 */
	@Test()
	public void testTranslate_09() throws InvalidConditionException
	{
		Condition condition = new Condition("person.firstName", EQUALS, "John");
		condition.and("person.address.street", EQUALS, "Main st.");
		ConditionTranslator ct = new ConditionTranslator(condition);
		QueryBuilder query = ct.translate();
		assertTrue("01", query instanceof BoolQueryBuilder);
		System.out.println(query.toString());
		String file = "ConditionTranslatorTest__testTranslate_09.json";
		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
		assertEquals("02", expected, query.toString());
	}
}
