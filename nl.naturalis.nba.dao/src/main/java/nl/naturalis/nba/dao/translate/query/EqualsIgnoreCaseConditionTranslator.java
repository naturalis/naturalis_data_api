package nl.naturalis.nba.dao.translate.query;

import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.ensureValueIsString;
import static nl.naturalis.nba.dao.translate.query.TranslatorUtil.getNestedPath;
import static org.elasticsearch.index.query.QueryBuilders.nestedQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	EqualsIgnoreCaseConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		String field = condition.getField();
		String value = condition.getValue().toString().toLowerCase();
		String nestedPath = getNestedPath(condition, mappingInfo);
		String multiField = field + '.' + IGNORE_CASE_MULTIFIELD.getName();
		if (nestedPath == null || forSortField) {
			return termQuery(multiField, value);
		}
		return nestedQuery(nestedPath, termQuery(multiField, value), ScoreMode.None);
	}

	@Override
	void checkCondition() throws InvalidConditionException
	{
		ensureValueIsString(condition);
	}

}
