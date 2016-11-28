package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(Condition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			return termQuery(field, value);
		}
		return nestedQuery(nestedPath, termQuery(field, value));
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
	}
}
