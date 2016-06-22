package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInspector;

public class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(Condition condition, MappingInspector inspector) throws InvalidConditionException
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (!inspector.isOperatorAllowed(field, EQUALS)) {
			throw new IllegalOperatorException(field.getName(), EQUALS);
		}
		String nestedPath = inspector.getNestedPath(field);
		if (nestedPath == null) {
			if (value() == null) {
				return boolQuery().mustNot(existsQuery(field()));
			}
			return termQuery(field(), value());
		}
		if (value() == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(field())));
		}
		return nestedQuery(nestedPath, termQuery(field(), value()));
	}
}
