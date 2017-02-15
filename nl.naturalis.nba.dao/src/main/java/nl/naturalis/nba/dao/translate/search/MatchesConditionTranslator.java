package nl.naturalis.nba.dao.translate.search;

import static nl.naturalis.nba.common.es.map.MultiField.DEFAULT_MULTIFIELD;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.SearchCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class MatchesConditionTranslator extends ConditionTranslator {

	private static final String MY_MULTIFIELD = DEFAULT_MULTIFIELD.getName();

	MatchesConditionTranslator(SearchCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		String field = path.append(MY_MULTIFIELD).toString();
		String value = condition.getValue().toString();
		return matchQuery(field, value);
	}

	@Override
	void preprocess() throws InvalidConditionException
	{
	}
}
