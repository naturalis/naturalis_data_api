package nl.naturalis.nba.dao.query;

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
		Object value = condition.getValue();
		String nestedPath = MappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			if (value == null) {
				return boolQuery().mustNot(existsQuery(path()));
			}
			return termQuery(path(), value);
		}
		if (value == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(path())));
		}
		return nestedQuery(nestedPath, termQuery(path(), value));
	}

	@Override
	void ensureOperatorValidForField() throws IllegalOperatorException
	{
	}

	@Override
	void ensureValueValidForOperator() throws InvalidConditionException
	{
	}
}
