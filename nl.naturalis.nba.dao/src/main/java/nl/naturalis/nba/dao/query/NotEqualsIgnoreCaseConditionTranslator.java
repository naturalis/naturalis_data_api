package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.StringField;

class NotEqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	NotEqualsIgnoreCaseConditionTranslator(Condition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		String value = condition.getValue().toString().toLowerCase();
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = field + '.' + IGNORE_CASE_MULTIFIELD.getName();
		if (nestedPath == null) {
			return boolQuery().mustNot(termQuery(multiField, value));
		}
		return boolQuery().mustNot(nestedQuery(nestedPath, termQuery(multiField, value)));
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
		ESField field = TranslatorUtil.getESField(condition, mappingInfo);
		if (field instanceof StringField) {
			StringField stringField = (StringField) field;
			if (stringField.hasMultiField(IGNORE_CASE_MULTIFIELD)) {
				return; /* OK */
			}
		}
		throw new IllegalOperatorException(condition);
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
		ensureValueIsString(condition);
	}

}
