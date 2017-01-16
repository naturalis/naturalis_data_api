package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class MatchesConditionTranslator extends ConditionTranslator {

	MatchesConditionTranslator(Condition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = condition.getField() + '.' + DEFAULT_MULTIFIELD.getName();
		String value = condition.getValue().toString().toLowerCase();
		if (nestedPath == null || forSortField) {
			return matchQuery(multiField, value);
		}
		return nestedQuery(nestedPath, matchQuery(multiField, value));
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsString(condition);
	}
}
