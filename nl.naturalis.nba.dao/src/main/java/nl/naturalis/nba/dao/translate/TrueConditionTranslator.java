package nl.naturalis.nba.dao.translate;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/*
 * Translates into an Elasticsearch query guraranteed to match all documents,
 * akin to SQL's "WHERE true", or "WHERE 1 = 1". See javadoc for
 * {@link QueryCondition}.
 */
class TrueConditionTranslator extends ConditionTranslator {

	TrueConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		return matchAllQuery();
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
