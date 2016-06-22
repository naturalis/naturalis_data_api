package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInspector;

public class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(Condition condition, MappingInspector inspector) throws InvalidConditionException
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (!inspector.isOperatorAllowed(field, LIKE)) {
			throw new IllegalOperatorException(field.getName(), LIKE);
		}
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (value().getClass() != String.class) {
			throw searchTermHasWrongType();
		}
		String value = (String) value();
		if (value.length() < 3) {
			throw error("Search term must contain at least 3 characters with operator %s", LIKE);
		}
		if (value.length() > 10) {
			throw error("Search term must contain at most 10 characters with operator %s", LIKE);
		}
		String nestedPath = inspector.getNestedPath(field);
		String multiField = field() + ".like";
		if (nestedPath == null) {
			return termQuery(multiField, value.toLowerCase());
		}
		return nestedQuery(nestedPath, termQuery(multiField, value.toLowerCase()));
	}
}
