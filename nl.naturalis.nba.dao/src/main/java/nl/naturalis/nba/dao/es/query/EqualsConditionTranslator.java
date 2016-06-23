package nl.naturalis.nba.dao.es.query;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.dao.es.map.MappingInfo;

public class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(Condition condition, MappingInfo inspector) throws InvalidConditionException
	{
		super(condition, inspector);
	}

	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String nestedPath = mappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			if (value() == null) {
				return boolQuery().mustNot(existsQuery(path()));
			}
			return termQuery(path(), value());
		}
		if (value() == null) {
			return nestedQuery(nestedPath, boolQuery().mustNot(existsQuery(path())));
		}
		return nestedQuery(nestedPath, termQuery(path(), value()));
	}
}
