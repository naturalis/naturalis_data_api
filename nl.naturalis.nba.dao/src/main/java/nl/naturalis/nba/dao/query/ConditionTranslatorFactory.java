package nl.naturalis.nba.dao.query;

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
				PrimitiveField pf = TranslatorUtil.getESField(condition, mappingInfo);
				switch (pf.getType()) {
					case GEO_POINT:
					case GEO_SHAPE:
						if (condition.getValue() instanceof GeoJsonObject) {
							return new InGeometryConditionTranslator(condition, mappingInfo);
						}
						if (condition.getValue().getClass() == String.class) {
							String s = condition.getValue().toString().trim();
							if (s.charAt(0) == '{') {
								try {

								}
								catch (JsonDeserializationException e) {

								}
							}
						}
					default:
						return new InValuesConditionTranslator(condition, mappingInfo);
				}
		}
		return null;
	}

}
