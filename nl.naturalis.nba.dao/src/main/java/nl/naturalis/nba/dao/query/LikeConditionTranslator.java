package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.query.TranslatorUtil.invalidConditionException;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(Condition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = condition.getField() + '.' + LIKE_MULTIFIELD.getName();
		String value = condition.getValue().toString().toLowerCase();
		if (nestedPath == null) {
			return termQuery(multiField, value);
		}
		return nestedQuery(nestedPath, termQuery(multiField, value));
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
		if (value.length() > 10) {
			String fmt = "Search term may contain no more than 10 characters with operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
	}
}
