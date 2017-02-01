package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.LIKE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.invalidConditionException;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LikeConditionTranslator extends ConditionTranslator {

	LikeConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		String field = path.append(LIKE_MULTIFIELD.getName()).toString();
		String value = condition.getValue().toString().toLowerCase();
		QueryBuilder query = termQuery(field, value);
		if (forSortField) {
			return query;
		}
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		if (condition.isFilter().booleanValue()) {
			query = constantScoreQuery(query);
		}
		else if (condition.getBoost() != null) {
			query.boost(condition.getBoost());
		}
		return query;
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsNotNull(condition);
		ensureValueIsString(condition);
		String value = condition.getValue().toString();
		//TODO: soft code upper and lower bounds of n-gram size
		if (value.length() < 3) {
			String fmt = "Search term must contain at least 3 characters with operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
		if (value.length() > 15) {
			String fmt = "Search term may contain no more than 15 characters with operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
	}
}
