package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.IGNORE_CASE_MULTIFIELD;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.ensureValueIsString;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class EqualsIgnoreCaseConditionTranslator extends ConditionTranslator {

	private static final String MY_MULTIFIELD = IGNORE_CASE_MULTIFIELD.getName();

	EqualsIgnoreCaseConditionTranslator(SearchCondition condition, MappingInfo<?> inspector)
	{
		super(condition, inspector);
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
		ensureValueIsString(condition);
	}

}
