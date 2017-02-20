package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS_IC;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import org.elasticsearch.index.query.NestedQueryBuilder;
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
public class EqualsIgnoreCaseConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	@BeforeClass
	public static void init()
	{
		Mapping<TestPerson> m = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(m);
	}

	/*
	 * Test EQUALS_IC with nested field.
	 */
	@Test
	public void testTranslateWithNestedField_01() throws InvalidConditionException
	{
		SearchCondition condition = new SearchCondition("addressBook.street", EQUALS_IC,
				"Market street");
		ConditionTranslator ct = getTranslator(condition, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof NestedQueryBuilder);
		String file = "translate/search/EqualsIgnoreCaseConditionTranslatorTest__testTranslateWithNestedField_01.json";
		assertTrue("02", queryEquals(query, file));
	}
}
