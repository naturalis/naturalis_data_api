package nl.naturalis.nba.dao.translate.search;

import static org.elasticsearch.index.query.QueryBuilders.existsQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * This ConditionTranslator is called when Condition.operator is NOT_EQUALS and
 * Condition.value is null. By not having this situation handled by the
 * {@link EqualsConditionTranslator}, we keep our code and the generated
 * Elasticsearch query easier to read.
 * 
 * @author Ayco Holleman
 *
 */
class IsNotNullConditionTranslator extends ConditionTranslator {

	IsNotNullConditionTranslator(SearchCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		return existsQuery(condition.getField().toString());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
