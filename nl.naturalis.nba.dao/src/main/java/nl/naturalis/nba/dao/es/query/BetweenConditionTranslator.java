package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class BetweenConditionTranslator extends ConditionTranslator {

	private static final String ERROR_0 = "When using operator %s, the search "
			+ "term must be a an array with exactly two elements";

	private static final String ERROR_1 = "When using operator %s, at least "
			+ "one of boundary values must not be null";

	BetweenConditionTranslator(Condition condition, MappingInfo inspector)
			throws InvalidConditionException
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (!value().getClass().isArray()) {
			throw error(ERROR_0, operator());
		}
		Object[] values = (Object[]) value();
		if (values.length != 2) {
			throw error(ERROR_0, operator());
		}
		if (values[0] == null && values[1] == null) {
			throw error(ERROR_1, operator());
		}
		RangeQueryBuilder query = QueryBuilders.rangeQuery(path());
		query.from(values[0]);
		query.to(values[1]);
		String nestedPath = mappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

}
