package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.ESDataType;
import nl.naturalis.nba.dao.es.map.ESField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.map.NoSuchFieldException;
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

	protected QueryBuilder translateCondition() throws InvalidConditionException
	{
		ESField f;
		try {
			f = inspector.getField(field());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		String nestedPath = inspector.getNestedPath(f);
		if (nestedPath == null) {
			return termQuery(field(), value());
		}
		return nestedQuery(nestedPath, termQuery(field(), value()));
	}
}
