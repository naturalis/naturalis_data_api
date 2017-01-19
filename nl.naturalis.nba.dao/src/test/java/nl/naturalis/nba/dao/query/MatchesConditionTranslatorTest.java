package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.api.ComparisonOperator.MATCHES;
import static nl.naturalis.nba.dao.ESTestUtils.queryEquals;
import static nl.naturalis.nba.dao.query.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.test.TestPerson;

public class MatchesConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<TestPerson> m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(m);
	}

	@Test
	public void testTranslate_01() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", MATCHES, "John");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof MatchQueryBuilder);
		String file = "MatchesConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	@Test(expected = InvalidConditionException.class)
	public void testTranslate_02() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", MATCHES, null);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof MatchQueryBuilder);
		String file = "MatchesConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	@Test(expected = InvalidConditionException.class)
	public void testTranslate_03() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", MATCHES, 7);
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof MatchQueryBuilder);
		String file = "MatchesConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

	@Test(expected = InvalidConditionException.class)
	public void testTranslate_04() throws InvalidConditionException
	{
		QueryCondition condition = new QueryCondition("firstName", MATCHES, new Date());
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof MatchQueryBuilder);
		String file = "MatchesConditionTranslatorTest__testTranslate_01.json";
		assertTrue("02", queryEquals(getClass(), query, file));
	}

}
