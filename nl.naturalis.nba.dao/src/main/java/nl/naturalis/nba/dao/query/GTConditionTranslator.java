package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class GTConditionTranslator extends RangeConditionTranslator {

	GTConditionTranslator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	void setRange(RangeQueryBuilder query)
	{
		query.gt(condition.getValue());
	}

}
