package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class GTConditionTranslator extends RangeConditionTranslator {

	GTConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	protected void setRange(RangeQueryBuilder query)
	{
		query.gt(value());
	}

}
