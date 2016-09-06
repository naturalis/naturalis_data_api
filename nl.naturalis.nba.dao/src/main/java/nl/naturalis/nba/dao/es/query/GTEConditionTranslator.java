package nl.naturalis.nba.dao.es.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.common.es.map.MappingInfo;

class GTEConditionTranslator extends RangeConditionTranslator {

	GTEConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	void setRange(RangeQueryBuilder query)
	{
		query.gte(value());
	}

}