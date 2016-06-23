package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import static nl.naturalis.nba.dao.es.map.MultiField.*;

public class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	EqualsIgnoreCaseConditionTranslator(Condition condition, MappingInspector inspector)
			throws InvalidConditionException
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (!inspector.isOperatorAllowed(field, operator())) {
			throw new IllegalOperatorException(field.getName(), operator());
		}
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (value().getClass() != String.class) {
			throw searchTermHasWrongType();
		}
		String nestedPath = inspector.getNestedPath(field);
		String multiField = field() + '.' + IGNORE_CASE_MULTIFIELD.getName();
		if (nestedPath == null) {
			if (value() == null) {
				return boolQuery().mustNot(existsQuery(field()));
			}
			return termQuery(multiField, value());
		}
		if (value() == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(field())));
		}
		return nestedQuery(nestedPath, termQuery(multiField, value()));
	}
}
