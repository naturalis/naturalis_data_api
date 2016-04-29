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
	 * <li>has three AND siblings, one using the EQUALS operator and two using the
	 * NOT_EQUALS operator.
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
	 * <li>uses the EQUALS operator
	 * <li>has one OR sibling also using the EQUALS operator
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
		System.out.println(query.toString());
//		String file = "ConditionTranslatorTest__testTranslate_05.json";
//		String expected = FileUtil.getContents(getClass().getResourceAsStream(file));
//		assertEquals("02", expected, query.toString());
	}
}
