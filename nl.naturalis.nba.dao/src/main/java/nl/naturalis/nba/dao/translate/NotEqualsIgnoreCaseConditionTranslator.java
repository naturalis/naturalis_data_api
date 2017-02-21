package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.ensureValueIsString;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class NotEqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	private static final String MY_MULTIFIELD = IGNORE_CASE_MULTIFIELD.getName();

	NotEqualsIgnoreCaseConditionTranslator(QueryCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		String field = path.append(MY_MULTIFIELD).toString();
		String value = condition.getValue().toString().toLowerCase();
		QueryBuilder query = termQuery(field, value);
		return boolQuery().mustNot(query);
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		ensureValueIsString(condition);
	}

}
