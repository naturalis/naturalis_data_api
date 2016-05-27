package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.ComparisonOperator.LIKE;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.DocumentField;
import nl.naturalis.nba.dao.es.map.MappingInspector;
import nl.naturalis.nba.dao.es.types.ESType;

public class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(Condition condition, Class<? extends ESType> forType)
	{
		super(condition, forType);
	}

	LikeConditionTranslator(Condition condition, MappingInspector inspector)
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if (!inspector.isOperatorAllowed(f, LIKE)) {
			throw new IllegalOperatorException(f.getName(), LIKE);
		}
		if (value() == null) {
			throw error(f, "Search term must not be null with operator %s", LIKE);
		}
		if (value().getClass() != String.class) {
			throw error(f, "Search term must have type java.lang.String with operator %s", LIKE);
		}
		String value = (String) value();
		if (value.length() < 3) {
			throw error(f, "Search term must contain at least 3 characters with operator %s", LIKE);
		}
		if (value.length() > 10) {
			throw error(f, "Search term must contain at most 10 characters with operator %s", LIKE);
		}
		String nestedPath = inspector.getNestedPath(f);
		String multiField = field() + ".like";
		if (nestedPath == null) {
			return termQuery(multiField, value.toLowerCase());
		}
		return nestedQuery(nestedPath, termQuery(multiField, value.toLowerCase()));
	}
}
