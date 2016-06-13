package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
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

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		DocumentField f = getDocumentField(field());
		if (!inspector.isOperatorAllowed(f, operator())) {
			throw new IllegalOperatorException(f.getName(), operator());
		}
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (value().getClass() != String.class) {
			throw searchTermHasWrongType();
		}
		String nestedPath = inspector.getNestedPath(f);
		String multiField = field() + ".ci";
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
