package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.api.ComparisonOperator.EQUALS;
import static nl.naturalis.nba.api.ComparisonOperator.LIKE;
import static nl.naturalis.nba.dao.DaoTestUtil.queryEquals;
import static nl.naturalis.nba.dao.translate.search.ConditionTranslatorFactory.getTranslator;
import static org.junit.Assert.assertTrue;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.junit.Test;

import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.api.SearchSpec;
import nl.naturalis.nba.common.es.map.Mapping;
import nl.naturalis.nba.common.es.map.MappingFactory;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.test.TestPerson;

@SuppressWarnings("static-method")
public class EqualsConditionTranslatorTest {

	private static MappingInfo<TestPerson> mappingInfo;

	static {
		Mapping<TestPerson> mapping = MappingFactory.getMapping(TestPerson.class);
		mappingInfo = new MappingInfo<>(mapping);
	}

	@Test
	public void testWithNullValue_01() throws InvalidQueryException
	{
		SearchCondition condition1 = new SearchCondition("firstName", EQUALS, null);
		condition1.setConstantScore(true);
		condition1.setBoost(2.3F);
		ConditionTranslator ct = getTranslator(condition1, mappingInfo);
		QueryBuilder query = ct.translate();
		//System.out.println(query);
		assertTrue("01", query instanceof BoolQueryBuilder);
		String file = "translate/search/EqualsConditionTranslatorTest__testWithNullValue_01.json";
		assertTrue("02", queryEquals(query, file));
	}

	//@Test
	public void testWithStringValue_02() throws InvalidQueryException
	{
		SearchCondition condition1 = new SearchCondition(
				"identifications.scientificName.genusOrMonomial", LIKE, "erica");
		condition1.setConstantScore(true);
		condition1.setBoost(2.3F);
		SearchCondition condition2 = new SearchCondition(
				"identifications.scientificName.specificEpithet", EQUALS, "benthamiana");
		SearchCondition condition3 = new SearchCondition("unitID", EQUALS, "WAG.1779778");
		ConditionTranslator ct = getTranslator(condition1, mappingInfo);
		SearchSpec ss = new SearchSpec();
		ss.addCondition(condition1.or(condition2.and(condition3)));
		//ss.addCondition(condition2);
		ss.setConstantScore(true);
		SearchRequestBuilder query = new SearchSpecTranslator(ss, DocumentType.SPECIMEN)
				.translate();
		System.out.println(query);

	}

}
