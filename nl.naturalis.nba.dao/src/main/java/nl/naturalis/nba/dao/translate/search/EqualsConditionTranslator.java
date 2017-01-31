package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.ESDataType.DATE;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.constantScoreQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsConditionTranslator extends ConditionTranslator {

	EqualsConditionTranslator(SearchCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		QueryBuilder query = termQuery(path.toString(), condition.getValue());
		String nestedPath = getNestedPath(path, mappingInfo);
		if (nestedPath != null && !forSortField) {
			query = nestedQuery(nestedPath, query, ScoreMode.None);
		}
		if (condition.isFilter().booleanValue()) {
			query = constantScoreQuery(query);
		}
		else {
			query.boost(condition.getBoost());
		}
		return query;
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		Path path = condition.getFields().iterator().next();
		ESField field = getESField(path, mappingInfo);
		if (field.getType() == DATE) {
			convertValueForDateField(condition);
		}
	}
}
