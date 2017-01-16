package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * This ConditionTranslator is called when Condition.operator is EQUALS and
 * Condition.value is null. By not having this situation handled by the
 * {@link EqualsConditionTranslator}, we keep our code and the generated
 * Elasticsearch query easier to read.
 * 
 * @author Ayco Holleman
 *
 */
class IsNullConditionTranslator extends ConditionTranslator {

	IsNullConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null || forSortField) {
			return boolQuery().mustNot(existsQuery(field));
		}
		/*
		 * NOTE: contrary to what the Elasticsearch documentation suggests about
		 * the existsQuery and especially about the now deprecated missingQuery,
		 * you cannot blindly wrap the existsQuery into a mustNot query to get
		 * its a negation (an IS NULL query). If you're also dealing with a
		 * nestedQuery, you must first wrap the existsQuery into the
		 * nestedQuery, and then wrap the nestedQuery into the mustNot query! If
		 * you do it the other way round, you will not get the desired &
		 * expected result.
		 */
		return boolQuery().mustNot(nestedQuery(nestedPath, existsQuery(field)));
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
	}
}
