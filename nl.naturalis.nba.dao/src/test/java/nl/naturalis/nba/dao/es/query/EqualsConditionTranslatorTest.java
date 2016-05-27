package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_EQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static nl.naturalis.nba.api.query.ComparisonOperator.*;
import static nl.naturalis.nba.api.query.UnaryBooleanOperator.*;

import org.domainobject.util.FileUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;

public class EqualsConditionTranslatorTest {

	//	@Test
	//	public void testTranslate_01() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", EQUALS, "Smith");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof TermQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_01.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	@Test
	//	public void testTranslate_02() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", EQUALS, "John");
	//		condition.and("lastName", EQUALS, "Smith");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_02.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	@Test
	//	public void testTranslate_03() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", NOT_EQUALS, "John");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_03.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	@Test
	//	public void testTranslate_04() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", EQUALS, "John");
	//		condition.and("lastName", EQUALS, "Smith");
	//		condition.and("hasChildren", NOT_EQUALS, true);
	//		condition.and("favoritePet", NOT_EQUALS, "dog");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_04.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	@Test
	//	public void testTranslate_05() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", EQUALS, "John");
	//		condition.or("lastName", EQUALS, "Smith");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_05.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	@Test
	//	public void testTranslate_06() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("firstName", NOT_EQUALS, "John");
	//		condition.or("lastName", NOT_EQUALS, "Smith");
	//		condition.or("favoritePet", EQUALS, "dog");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, Dummy01.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_06.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	/*
	//	 * Tests deep nesting of conditions.
	//	 * 
	//	 * @throws InvalidConditionException
	//	 */
	//	@Test
	//	public void testTranslate_07() throws InvalidConditionException
	//	{
	//
	//		Condition deepest = new Condition("pet", EQUALS, "parrot");
	//		deepest.and("color", EQUALS, "blue").and("talks", EQUALS, "true");
	//
	//		Condition deeper = new Condition("pet", EQUALS, "dog");
	//		deeper.or("pet", EQUALS, "cat").or(deepest);
	//
	//		Condition condition = new Condition("firstName", EQUALS, "John");
	//		condition.and("lastName", NOT_EQUALS, "Smith").and(deeper);
	//
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
	//		QueryBuilder query = ct.translate();
	//		//System.out.println(query);
	//		assertTrue("01", query instanceof BoolQueryBuilder);
	//		String file = "ConditionTranslatorTest__testTranslate_07.json";
	//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
	//		assertEquals("02", expected.trim(), query.toString().trim());
	//	}
	//
	//	/*
	//	 * Tests combination of AND and OR siblings.
	//	 * 
	//	 * @throws InvalidConditionException
	//	 */
	//	@Test
	//	public void testTranslate_08() throws InvalidConditionException
	//	{
	//		Condition condition = new Condition("pet", EQUALS, "parrot");
	//		condition.and("color", EQUALS, "blue");
	//		condition.and("firstName", EQUALS, "John");
	//		condition.or("talks", EQUALS, "true");
	//		condition.or("lastName", EQUALS, "Smith");
	//		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
	//		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
	//		QueryBuilder query = ct.translate();
	//		System.out.println(query);
	//	}

	/*
	 * Tests that comparing field with null using EQUALS operator results in
	 * ExistsQuery being generated.
	 */
	//@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", EQUALS, null);
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_01.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator results in
	 * a doubly negated ExistsQuery being generated.
	 */
	//@Test
	public void testTranslate_02() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", NOT_EQUALS, null);
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_02.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Tests that comparing field with null using EQUALS operator and NOT
	 * operator results in a doubly negated ExistsQuery being generated.
	 */
	//@Test
	public void testTranslate_03() throws InvalidConditionException
	{
		Condition condition = new Condition(NOT, "firstName", EQUALS, null);
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_03.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Tests that comparing field with null using NOT_EQUALS operator and NOT
	 * operator results in a triply negated ExistsQuery being generated.
	 */
	@Test
	public void testTranslate_04() throws InvalidConditionException
	{
		Condition condition = new Condition(NOT, "firstName", NOT_EQUALS, null);
		ConditionTranslatorFactory ctf = new ConditionTranslatorFactory();
		ConditionTranslator ct = ctf.getTranslator(condition, EqualsTestObject.class);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "EqualsConditionTranslatorTest__testTranslate_04.json";
		assertEquals("02", getContents(file), query.toString());
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
