package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.es.map.ESDataType.DATE;
import static nl.naturalis.nba.dao.query.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;

class NotEqualsConditionTranslator extends ConditionTranslator {

	NotEqualsConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		Object value = condition.getValue();
		String nestedPath = getNestedPath(condition, mappingInfo);
		TermQueryBuilder query = termQuery(field, value);
		if (nestedPath == null || forSortField) {
			return boolQuery().mustNot(query);
		}
		return boolQuery().mustNot(nestedQuery(nestedPath, query, ScoreMode.None));
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ESField field = getESField(condition, mappingInfo);
		if (field.getType() == DATE) {
			convertValueForDateField(condition);
		}
	}
}
