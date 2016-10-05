package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.query.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.domainobject.util.FileUtil;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.query.ConditionTranslator;
import nl.naturalis.nba.dao.test.Address;
import nl.naturalis.nba.dao.test.TestPerson;

@SuppressWarnings("static-method")
public class BetweenConditionTranslatorTest {

	private static MappingInfo mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo(m);
	}

	/*
	 * Checks that translation fails if the Condition.field is not a number or
	 * date.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01a() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", BETWEEN, "John");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the Condition.field is not a number or
	 * date.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01b() throws InvalidConditionException
	{
		Condition condition = new Condition("smoker", BETWEEN, true);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the Condition.field is not a number or
	 * date.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01c() throws InvalidConditionException
	{
		Condition condition = new Condition("address", BETWEEN, new Address());
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the Condition.value is not a two-element
	 * array.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02a() throws InvalidConditionException
	{
		Condition condition = new Condition("numChildren", BETWEEN, new int[] { 0 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if the Condition.value is not a two-element
	 * array.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02b() throws InvalidConditionException
	{
		Condition condition = new Condition("firstName", BETWEEN, new int[] { 0, 1, 2 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that it works for arrays and java.util.Collection instances
	 * containing 2 elements.
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_03a() throws InvalidConditionException
	{
		Condition condition = new Condition("numChildren", BETWEEN, new int[] { 2, 8 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		System.out.println(query);
		assertTrue("01", query instanceof RangeQueryBuilder);
		String file = "BetweenConditionTranslatorTest__testTranslate_03.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Checks that it works for arrays and java.util.Collection instances
	 * containing 2 elements.
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_03b() throws InvalidConditionException
	{
		Condition condition = new Condition("numChildren", BETWEEN, Arrays.asList(2, 8));
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof RangeQueryBuilder);
		String file = "BetweenConditionTranslatorTest__testTranslate_03.json";
		assertEquals("02", getContents(file), query.toString());
	}

	/*
	 * Do some tests with NOT_BETWEEN
	 * 
	 * @throws InvalidConditionException
	 */
	@Test
	public void testTranslate_04a() throws InvalidConditionException
	{
		Set<Integer> set = new LinkedHashSet<>(Arrays.asList(30, 40));
		Condition condition = new Condition("address.country.dialNumber", NOT_BETWEEN, set);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		// System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "BetweenConditionTranslatorTest__testTranslate_04.json";
		assertEquals("02", getContents(file), query.toString());
	}

	private String getContents(String file)
	{
		return FileUtil.getContents(getClass().getResourceAsStream(file));
	}
}
