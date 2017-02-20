package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsNotNull;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LikeConditionTranslator extends ConditionTranslator {

	private static final String MY_MULTIFIELD = LIKE_MULTIFIELD.getName();

	LikeConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		String field = path.append(MY_MULTIFIELD).toString();
		String value = condition.getValue().toString().toLowerCase();
		return termQuery(field, value);
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		String value = condition.getValue().toString();
		//TODO: soft code upper and lower bounds of n-gram size
		if (value.length() < 3) {
			String fmt = "Search term must contain at least 3 characters with operator %s";
			throw new InvalidConditionException(condition, fmt, condition.getOperator());
		}
		if (value.length() > 15) {
			String fmt = "Search term may contain no more than 15 characters with operator %s";
			throw new InvalidConditionException(condition, fmt, condition.getOperator());
		}
	}
}
