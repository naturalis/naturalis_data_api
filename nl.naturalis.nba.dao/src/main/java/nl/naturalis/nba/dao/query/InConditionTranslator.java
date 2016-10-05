package nl.naturalis.nba.dao.query;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates conditions with an IN or NOT_IN operator.
 * 
 * @author Ayco Holleman
 *
 */
class InConditionTranslator extends ConditionTranslator {

	InConditionTranslator(Condition condition, MappingInfo inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		InValuesBuilder ivb = new InValuesBuilder(value());
		QueryBuilder query;
		if (ivb.containsNull()) {
			if (ivb.getValues().size() == 0)
				query = isNull();
			else
				query = isNullOrOneOf(ivb.getValues());
		}
		else {
			query = isOneOf(ivb.getValues());
		}
		String nestedPath = MappingInfo.getNestedPath(field());
		if (nestedPath == null) {
			return query;
		}
		return nestedQuery(nestedPath, query);
	}

	public QueryBuilder isNullOrOneOf(List<?> values)
	{
		BoolQueryBuilder boolQuery = boolQuery();
		boolQuery.should(isOneOf(values));
		boolQuery.should(isNull());
		return boolQuery;
	}

	TermsQueryBuilder isOneOf(List<?> values)
	{
		return termsQuery(path(), values);
	}

	BoolQueryBuilder isNull()
	{
		return boolQuery().mustNot(existsQuery(path()));
	}

}
