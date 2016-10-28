package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			if (value == null) {
				return boolQuery().mustNot(existsQuery(field));
			}
			return termQuery(field, value);
		}
		if (value == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(field)));
		}
		return nestedQuery(nestedPath, termQuery(field, value));
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
	}
}
