package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.QueryBuilder;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

class InGeoAreaConditionTranslator extends ConditionTranslator {

	public InGeoAreaConditionTranslator(Condition condition, MappingInfo mappingInfo)
	{
		super(condition, mappingInfo);
	}

	@Override
	QueryBuilder translateCondition() throws InvalidConditionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void checkOperatorFieldCombi() throws IllegalOperatorException
	{
	}

	@Override
	void checkOperatorValueCombi() throws InvalidConditionException
	{
	}

}