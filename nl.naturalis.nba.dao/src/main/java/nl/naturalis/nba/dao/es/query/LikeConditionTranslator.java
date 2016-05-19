package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.Operator.LIKE;
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

	protected QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if(!inspector.isOperatorAllowed(f, LIKE)) {
			throw new IllegalOperatorException(f.getName(), LIKE);
		}
		String nestedPath = inspector.getNestedPath(f);
		// TODO: soft-code lookup operator => multifield name.
		String multiField = field() + ".like";
		if (nestedPath == null) {
			return termQuery(multiField, value());
		}
		return nestedQuery(nestedPath, termQuery(multiField, value()));
	}
}
