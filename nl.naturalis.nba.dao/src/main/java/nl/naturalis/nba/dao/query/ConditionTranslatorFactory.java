package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;

import java.util.Collection;

import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.dao.DocumentType;

public class ConditionTranslatorFactory {

	private ConditionTranslatorFactory()
	{
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition condition}
	 * and the specified {@link DocumentType document type}.
	 * 
	 * @param condition
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(Condition condition,
			DocumentType<?> type) throws InvalidConditionException
	{
		return getTranslator(condition, new MappingInfo(type.getMapping()));
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified {@link Condition condition}
	 * and the specified {@link MappingInfo} object.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(Condition condition,
			MappingInfo mappingInfo) throws InvalidConditionException
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
		Object val = condition.getValue();
		if (val == null || val.getClass().isArray() || val instanceof Collection) {
			return new InValuesConditionTranslator(condition, mappingInfo);
		}
		SimpleField pf = getESField(condition, mappingInfo);
		switch (pf.getType()) {
			case GEO_POINT:
				if (val instanceof GeoJsonObject) {
					return new PointInShapeConditionTranslator(condition, mappingInfo);
				}
				if (isJson(val)) {
					condition.setValue(getGeoJsonObject(val));
					return new PointInShapeConditionTranslator(condition, mappingInfo);
				}
			case GEO_SHAPE:
				if (val instanceof GeoJsonObject) {
					return new ShapeInShapeConditionTranslator(condition, mappingInfo);
				}
				if (isJson(val)) {
					condition.setValue(getGeoJsonObject(val));
					return new ShapeInShapeConditionTranslator(condition, mappingInfo);
				}
				return new ShapeInGeoAreaConditionTranslator(condition, mappingInfo);
			default:
				return new InValuesConditionTranslator(condition, mappingInfo);
		}
	}

	/*
	 * Whether or not we must assume that Condition.value is a JSON string, or at least
	 * not a geographical name like "Amsterdam". The assumption is that no geographical
	 * name starts with '{' and ends with '}', which seems safe.
	 */
	private static boolean isJson(Object val)
	{
		if (val.getClass() == String.class) {
			String s = val.toString().trim();
			return s.charAt(0) != '{' && s.charAt(s.length() - 1) != '}';
		}
		return false;
	}

	private static GeoJsonObject getGeoJsonObject(Object val)
			throws InvalidConditionException
	{
		try {
			return deserialize(val.toString(), GeoJsonObject.class);
		}
		catch (JsonDeserializationException e) {
			String fmt = "Not a valid GeoJSON string (%s):\n%s";
			String msg = String.format(fmt, e.getMessage(), val);
			throw new InvalidConditionException(msg);
		}

	}

}
