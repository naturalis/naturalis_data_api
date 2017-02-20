package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.ESDataType.DATE;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.convertValuesForDateField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getESField;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.existsQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates conditions with an IN or NOT_IN operator when used with non-Geo
 * data types.
 * 
 * @author Ayco Holleman
 *
 */
class InConditionTranslator extends ConditionTranslator {

	InConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Exploder exploder = new Exploder(condition.getValue());
		if (exploder.containsNull()) {
			if (exploder.getValues().size() == 0) {
				return isNull();
			}
			return isNullOrOneOf(exploder.getValues());
		}
		return isOneOf(exploder.getValues());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ESField field = getESField(condition, mappingInfo);
		if (field.getType() == DATE) {
			convertValuesForDateField(condition);
		}
	}

	private QueryBuilder isNullOrOneOf(List<?> values)
	{
		BoolQueryBuilder boolQuery = boolQuery();
		boolQuery.should(isOneOf(values));
		boolQuery.should(isNull());
		return boolQuery;
	}

	private TermsQueryBuilder isOneOf(List<?> values)
	{
		String field = condition.getField().toString();
		return termsQuery(field, values);
	}

	private BoolQueryBuilder isNull()
	{
		String field = condition.getField().toString();
		return boolQuery().mustNot(existsQuery(field));
	}

}
