package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.query.TranslatorUtil.invalidConditionException;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.StringField;

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
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
		ESField field = getESField(condition, mappingInfo);
		if (field.getType() != ESDataType.STRING) {
			throw new IllegalOperatorException(condition);
		}
		if (field.getType() != ESDataType.STRING) {
			throw new IllegalOperatorException(condition);
		}
		if (field instanceof StringField) {
			StringField af = (StringField) field;
			if (af.hasMultiField(LIKE_MULTIFIELD)) {
				return;
			}
		}
		throw new IllegalOperatorException(condition);
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
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
