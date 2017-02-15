package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.api.ComparisonOperator.BETWEEN;
import static nl.naturalis.nba.api.ComparisonOperator.NOT_BETWEEN;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.test.Address;
import nl.naturalis.nba.dao.test.TestPerson;
import nl.naturalis.nba.dao.translate.query.ConditionTranslator;

@SuppressWarnings("static-method")
public class BetweenConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<TestPerson> m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(m);
	}

	/*
	 * Checks that translation fails if Condition.field is not a number or date.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01a() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", BETWEEN, "John");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if Condition.field is not a number or date.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_01b() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("smoker", BETWEEN, true);
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
		QueryCondition condition = new QueryCondition("address", BETWEEN, new Address());
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if Condition.value is not a two-element
	 * array.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02a() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("numChildren", BETWEEN, new int[] { 0 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Checks that translation fails if Condition.value is not a two-element
	 * array.
	 */
	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02b() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", BETWEEN, new int[] { 0, 1, 2 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		ct.translate();
	}

	/*
	 * Test happy flow - Condition.value is array or java.util.Collection with 2
	 * elements.
	 */
	@Test
	public void testTranslate_03a() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("numChildren", BETWEEN, new int[] { 2, 8 });
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof RangeQueryBuilder);
		String file = "translate/query/BetweenConditionTranslatorTest__testTranslate_03.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test happy flow - Condition.value is array or java.util.Collection with 2
	 * elements.
	 */
	@Test
	public void testTranslate_03b() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("numChildren", BETWEEN, Arrays.asList(2, 8));
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof RangeQueryBuilder);
		String file = "translate/query/BetweenConditionTranslatorTest__testTranslate_03.json";
		assertTrue("02", queryEquals(query, file));
	}

	/*
	 * Test happy flow with NOT_BETWEEN.
	 */
	@Test
	public void testTranslate_04a() throws InvalidConditionException
	{
		Set<Integer> set = new LinkedHashSet<>(Arrays.asList(30, 40));
		QueryCondition condition = new QueryCondition("address.country.dialNumber", NOT_BETWEEN,
				set);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/query/BetweenConditionTranslatorTest__testTranslate_04.json";
		assertTrue("02", queryEquals(query, file));
	}
}
