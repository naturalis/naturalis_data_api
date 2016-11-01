package nl.naturalis.nba.dao.query;

import org.elasticsearch.index.query.QueryBuilder;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Translates conditions with an IN or NOT_IN operator when used with fields of type
 * {@link GeoJsonObject} and with a {@link Condition#getValue() search term of type
 * {@link String}, supposedly specifying a geographical name like "Amsterdam" or "France".
 * 
 * @author Ayco Holleman
 *
 */
class ShapeInGeoAreaConditionTranslator extends ConditionTranslator {

	public ShapeInGeoAreaConditionTranslator(Condition condition, MappingInfo mappingInfo)
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
