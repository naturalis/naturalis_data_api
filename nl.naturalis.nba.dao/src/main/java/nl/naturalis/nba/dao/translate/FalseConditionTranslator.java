package nl.naturalis.nba.dao.translate;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/*
 * Translates into an Elasticsearch query guraranteed to NOT match any documents,
 * akin to SQL's "WHERE false", or "WHERE 1 = 0". See javadoc for
 * {@link QueryCondition}.
 */
class FalseConditionTranslator extends ConditionTranslator {

	FalseConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		return boolQuery().mustNot(matchAllQuery());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
