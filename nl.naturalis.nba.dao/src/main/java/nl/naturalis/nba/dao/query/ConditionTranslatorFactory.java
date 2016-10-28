package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.dao.query.TranslatorUtil.ensureValueIsNotNull;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.PrimitiveField;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.dao.DocumentType;

public class ConditionTranslatorFactory {

	private ConditionTranslatorFactory()
	{
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition
	 * condition} and the specified {@link DocumentType document type}.
	 * 
	 * @param condition
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(Condition condition, DocumentType<?> type)
			throws InvalidConditionException
	{
		return getTranslator(condition, new MappingInfo(type.getMapping()));
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition
	 * condition} and the specified {@link MappingInfo} object.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(Condition condition, MappingInfo mappingInfo)
			throws InvalidConditionException
	{
		new FieldCheck(condition, mappingInfo).execute();
		switch (condition.getOperator()) {
			case EQUALS:
			case NOT_EQUALS:
				return new EqualsConditionTranslator(condition, mappingInfo);
			case EQUALS_IC:
			case NOT_EQUALS_IC:
				return new EqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
			case GT:
				return new GTConditionTranslator(condition, mappingInfo);
			case GTE:
				return new GTEConditionTranslator(condition, mappingInfo);
			case LT:
				return new LTConditionTranslator(condition, mappingInfo);
			case LTE:
				return new LTEConditionTranslator(condition, mappingInfo);
			case BETWEEN:
			case NOT_BETWEEN:
				return new BetweenConditionTranslator(condition, mappingInfo);
			case LIKE:
			case NOT_LIKE:
				return new LikeConditionTranslator(condition, mappingInfo);
			case IN:
			case NOT_IN:
				return getInConditionTranslator(condition, mappingInfo);
		}
		return null;
	}

	private static ConditionTranslator getInConditionTranslator(Condition condition,
			MappingInfo mappingInfo) throws InvalidConditionException
	{
		PrimitiveField pf = getESField(condition, mappingInfo);
		switch (pf.getType()) {
			case GEO_POINT:
			case GEO_SHAPE:
				ensureValueIsNotNull(condition);
				if (condition.getValue() instanceof GeoJsonObject) {
					return new InGeometryConditionTranslator(condition, mappingInfo);
				}
				if (condition.getValue().getClass() == String.class) {
					String s = condition.getValue().toString().trim();
					if (s.charAt(0) == '{') {
						try {
							GeoJsonObject geo = deserialize(s, GeoJsonObject.class);
							condition.setValue(geo);
							return new InGeometryConditionTranslator(condition, mappingInfo);
						}
						catch (JsonDeserializationException e) {
							String fmt = "Not a valid GeoJSON string (%s):\n%s";
							String msg = String.format(fmt, e.getMessage(), s);
							throw new InvalidConditionException(msg);
						}
					}
					return new InGeoAreaConditionTranslator(condition, mappingInfo);
				}
			default:
				return new InValuesConditionTranslator(condition, mappingInfo);
		}
	}

}
