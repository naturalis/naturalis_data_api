package nl.naturalis.nba.dao.es.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESType;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;

public class BetweenConditionTranslator extends ConditionTranslator {

	private static final String ERROR_0 = "Operator %s requires \"value\" field "
			+ "to contain a two-element array with exactly two non-null elements";

	public BetweenConditionTranslator(Condition condition, Class<? extends ESType> forType)
	{
		super(condition, forType);
	}

	public BetweenConditionTranslator(Condition condition, MappingInspector inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if (!inspector.isOperatorAllowed(f, operator())) {
			throw new IllegalOperatorException(f.getName(), operator());
		}
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
			throw error(ERROR_0, operator());
		}
		RangeQueryBuilder query = QueryBuilders.rangeQuery(field());
		query.from("TO DO");
		String nestedPath = inspector.getNestedPath(f);
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

}
