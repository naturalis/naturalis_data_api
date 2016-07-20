package nl.naturalis.nba.dao.es.query;

import static nl.naturalis.nba.api.query.LogicalOperator.AND;
import static nl.naturalis.nba.dao.es.query.ConditionTranslatorFactory.getTranslator;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.es.DocumentType;

public class QuerySpecTranslator {

	private QuerySpec qs;
	private DocumentType dt;

	public QuerySpecTranslator(QuerySpec querySpec, DocumentType documentType)
	{
		this.qs = querySpec;
		this.dt = documentType;
	}

	public QueryBuilder translate() throws InvalidConditionException
	{
		List<Condition> conditions = qs.getConditions();
		if (conditions == null || conditions.size() == 0) {
			return QueryBuilders.matchAllQuery();
		}
		if (conditions.size() == 1) {
			Condition c = conditions.iterator().next();
			return getTranslator(c, dt).translate();
		}
		BoolQueryBuilder result = QueryBuilders.boolQuery();
		if (qs.getLogicalOperator() == AND) {
			for (Condition c : conditions) {
				result.must(getTranslator(c, dt).translate());
			}
		}
		else {
			for (Condition c : conditions) {
				result.should(getTranslator(c, dt).translate());
			}
		}
		return result;
	}
}
