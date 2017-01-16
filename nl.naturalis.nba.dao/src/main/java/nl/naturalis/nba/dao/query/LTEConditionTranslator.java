package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class LTEConditionTranslator extends RangeConditionTranslator {

	LTEConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	void setRange(RangeQueryBuilder query)
	{
		query.lte(condition.getValue());
	}

}
