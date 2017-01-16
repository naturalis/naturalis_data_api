package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LTConditionTranslator extends RangeConditionTranslator {

	LTConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	void setRange(RangeQueryBuilder query)
	{
		query.lt(condition.getValue());
	}

}
