package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureFieldIsDateOrNumber;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.query.TranslatorUtil.invalidConditionException;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates conditions with a BETWEEN or NOT_BETWEEN operator.
 * 
 * @author Ayco Holleman
 *
 */
class BetweenConditionTranslator extends ConditionTranslator {

	private static final String ERROR_0 = "When using operator %s, the "
			+ "value field of the condition must be an array or Collection "
			+ "with exactly two elements";

	private static final String ERROR_1 = "When using operator %s, at least "
			+ "one of the boundary values must not be null";

	BetweenConditionTranslator(Condition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		ComparisonOperator operator = condition.getOperator();
		Object value = condition.getValue();
		Object min;
		Object max;
		if (value.getClass().isArray()) {
			if (Array.getLength(value) != 2) {
				throw invalidConditionException(condition, ERROR_0, operator);
			}
			min = Array.get(value, 0);
			max = Array.get(value, 1);
		}
		else if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>) value;
			if (collection.size() != 2)
				throw invalidConditionException(condition, ERROR_0, operator);
			Iterator<?> iterator = collection.iterator();
			min = iterator.next();
			max = iterator.next();
		}
		else {
			throw invalidConditionException(condition, ERROR_0, operator);
		}
		if (min == null && max == null) {
			throw invalidConditionException(condition, ERROR_1, operator);
		}
		String field = condition.getField();
		RangeQueryBuilder query = QueryBuilders.rangeQuery(field);
		query.from(min);
		query.to(max);
		String nestedPath = getNestedPath(condition, mappingInfo);
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
		ensureFieldIsDateOrNumber(condition, mappingInfo);
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
	}
}
