package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.AnalyzableField;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;

class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	EqualsIgnoreCaseConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = field + '.' + IGNORE_CASE_MULTIFIELD.getName();
		if (nestedPath == null) {
			if (value == null) {
				return boolQuery().mustNot(existsQuery(field));
			}
			return termQuery(multiField, value.toString().toLowerCase());
		}
		if (value == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(field)));
		}
		String str = value.toString().toLowerCase();
		return nestedQuery(nestedPath, termQuery(multiField, str));
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
		ESField field = null;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happend, already checked
			assert (false);
		}
		if (field.getType() != ESDataType.STRING) {
			throw new IllegalOperatorException(condition);
		}
		if (field instanceof AnalyzableField) {
			AnalyzableField af = (AnalyzableField) field;
			if (af.hasMultiField(IGNORE_CASE_MULTIFIELD)) {
				return;
			}
		}
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
		if (condition.getValue() != null) {
			ensureValueIsString(condition);
		}
	}

}
