package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class MatchesConditionTranslator extends ConditionTranslator {

	MatchesConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
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
		return nestedQuery(nestedPath, matchQuery(multiField, value), ScoreMode.None);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsString(condition);
	}
}
