package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.dao.translate.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.ensureValueIsString;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class StartsWithConditionTranslator extends ConditionTranslator {

	StartsWithConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		return prefixQuery(path.toString(), condition.getValue().toString());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsString(condition);
	}
}
