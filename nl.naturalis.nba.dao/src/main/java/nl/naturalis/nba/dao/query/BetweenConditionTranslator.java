package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureFieldIsDateOrNumber;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

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
			+ "one of boundary values must not be null";

	BetweenConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		Object val0;
		Object val1;
		if (value().getClass().isArray()) {
			if (Array.getLength(value()) != 2)
				throw error(ERROR_0, operator());
			val0 = Array.get(value(), 0);
			val1 = Array.get(value(), 1);
		}
		else if (value() instanceof Collection) {
			Collection<?> collection = (Collection<?>) value();
			if (collection.size() != 2)
				throw error(ERROR_0, operator());
			Iterator<?> iterator = collection.iterator();
			val0 = iterator.next();
			val1 = iterator.next();
		}
		else {
			throw error(ERROR_0, operator());
		}
		if (val0 == null && val1 == null) {
			throw error(ERROR_1, operator());
		}
		RangeQueryBuilder query = QueryBuilders.rangeQuery(path());
		query.from(val0);
		query.to(val1);
		String nestedPath = MappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}


	@Override
	void ensureFieldCompatibleWithOperator() throws IllegalOperatorException
	{
		ensureFieldIsDateOrNumber(condition, mappingInfo);
	}
}
