package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.EQUALS;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.*;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESType;

public class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(Condition condition, Class<? extends ESType> forType)
	{
		super(condition, forType);
	}

	EqualsConditionTranslator(Condition condition, MappingInspector inspector)
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if (!inspector.isOperatorAllowed(f, EQUALS)) {
			throw new IllegalOperatorException(f.getName(), EQUALS);
		}
		String nestedPath = inspector.getNestedPath(f);
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
