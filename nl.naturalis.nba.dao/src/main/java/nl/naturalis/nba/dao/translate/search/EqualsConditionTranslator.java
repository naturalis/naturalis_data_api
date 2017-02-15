package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.ESDataType.DATE;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.convertValueForDateField;
import static nl.naturalis.nba.dao.translate.search.TranslatorUtil.getESField;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

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
		Path path = condition.getField();
		return termQuery(path.toString(), condition.getValue());
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
		Path path = condition.getField();
		ESField field = getESField(path, mappingInfo);
		if (field.getType() == DATE) {
			convertValueForDateField(condition);
		}
	}
}
