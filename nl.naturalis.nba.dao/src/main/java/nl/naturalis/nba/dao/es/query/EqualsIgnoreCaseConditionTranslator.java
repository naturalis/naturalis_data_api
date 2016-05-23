package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.Operator.*;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESType;

public class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	EqualsIgnoreCaseConditionTranslator(Condition condition, Class<? extends ESType> forType)
	{
		super(condition, forType);
	}

	EqualsIgnoreCaseConditionTranslator(Condition condition, MappingInspector inspector)
	{
		super(condition, inspector);
	}

	protected QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if (!inspector.isOperatorAllowed(f, EQUALS_IC)) {
			throw new IllegalOperatorException(f.getName(), EQUALS_IC);
		}
		String nestedPath = inspector.getNestedPath(f);
		// TODO: soft-code lookup operator => multifield name.
		String multiField = field() + ".ci";
		if (nestedPath == null) {
			return termQuery(multiField, value());
		}
		return nestedQuery(nestedPath, termQuery(multiField, value()));
	}
}
