package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.ESDataType;
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
		ESDataType dataType;
		try {
			dataType = inspector.getType(field());
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		if (dataType == ESDataType.NESTED) {
			int i = field().lastIndexOf('.');
			String path = field().substring(0, i);
			return nestedQuery(path, termQuery(field(), value()));
		}
		return termQuery(field(), value());
	}
}
