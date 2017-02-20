package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.invalidConditionException;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = condition.getField() + '.' + LIKE_MULTIFIELD.getName();
		String value = condition.getValue().toString().toLowerCase();
		if (nestedPath == null || forSortField) {
			return termQuery(multiField, value);
		}
		return nestedQuery(nestedPath, termQuery(multiField, value), ScoreMode.None);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsString(condition);
		String value = condition.getValue().toString();
		if (value.length() < 3) {
			String fmt = "Search term must contain at least 3 characters with operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
		if (value.length() > 15) {
			String fmt = "Search term may contain no more than 15 characters with operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
	}
}