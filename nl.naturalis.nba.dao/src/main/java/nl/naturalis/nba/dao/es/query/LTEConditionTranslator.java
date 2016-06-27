package nl.naturalis.nba.dao.es.query;

import org.elasticsearch.index.query.RangeQueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.dao.es.map.MappingInfo;

class LTEConditionTranslator extends RangeConditionTranslator {

	LTEConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	void setRange(RangeQueryBuilder query)
	{
		query.lte(value());
	}

}
