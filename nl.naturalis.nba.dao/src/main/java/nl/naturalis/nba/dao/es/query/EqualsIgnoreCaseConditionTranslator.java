package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.dao.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	EqualsIgnoreCaseConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		if (value() == null) {
			throw searchTermMustNotBeNull();
		}
		if (value().getClass() != String.class) {
			throw searchTermHasWrongType();
		}
		String nestedPath = mappingInfo.getNestedPath(field());
		String multiField = path() + '.' + IGNORE_CASE_MULTIFIELD.getName();
		if (nestedPath == null) {
			if (value() == null) {
				return boolQuery().mustNot(existsQuery(path()));
			}
			return termQuery(multiField, value());
		}
		if (value() == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(path())));
		}
		return nestedQuery(nestedPath, termQuery(multiField, value()));
	}
}
