package nl.naturalis.nba.dao.translate;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class NotEqualsConditionTranslator extends ConditionTranslator {

	NotEqualsConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField().toString();
		QueryBuilder query = termQuery(field, condition.getValue());
		return boolQuery().mustNot(query);
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
