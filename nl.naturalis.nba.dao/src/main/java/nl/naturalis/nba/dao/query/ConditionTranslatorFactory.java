package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.query.TranslatorUtil.getESFieldType;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.dao.DocumentType;

public class ConditionTranslatorFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = getLogger(ConditionTranslatorFactory.class);

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
		return getTranslator(condition, new MappingInfo<>(type.getMapping()));
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
	public static ConditionTranslator getTranslator(Condition condition, MappingInfo<?> mappingInfo)
			throws InvalidConditionException
	{
		new FieldCheck(condition, mappingInfo).execute();
		checkOperator(condition, mappingInfo);
		switch (condition.getOperator()) {
			case EQUALS:
				if (condition.getValue() == null) {
					return new IsNullConditionTranslator(condition, mappingInfo);
				}
				return new EqualsConditionTranslator(condition, mappingInfo);
			case NOT_EQUALS:
				if (condition.getValue() == null) {
					return new IsNotNullConditionTranslator(condition, mappingInfo);
				}
				return new NotEqualsConditionTranslator(condition, mappingInfo);
			case EQUALS_IC:
				if (condition.getValue() == null) {
					return new IsNullConditionTranslator(condition, mappingInfo);
				}
				if (getESFieldType(condition, mappingInfo) == ESDataType.STRING) {
					return new EqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
				}
				/*
				 * This is a gesture to the user. The equals-ignore-case
				 * comparator naturally only makes sense for strings. So for
				 * numbers, dates, etc. we tacitly submit the condition to the
				 * EqualsConditionTranslator instead of the
				 * EqualsIgnoreCaseConditionTranslator. This also allows for a
				 * high-level instruction like "Do a case-insensitive query" -
				 * i.e. ALL conditions in the QuerySpec should be handled in a
				 * case-insensitive manner WHERE APPLICABLE (which is in case of
				 * string fields). This is what the _ignoreCase query parameter
				 * achieves. See the HttpQuerySpecBuilder class in the rest
				 * module.
				 */
				return new EqualsConditionTranslator(condition, mappingInfo);
			case NOT_EQUALS_IC:
				if (condition.getValue() == null) {
					return new IsNotNullConditionTranslator(condition, mappingInfo);
				}
				if (getESFieldType(condition, mappingInfo) == ESDataType.STRING) {
					return new NotEqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
				}
				return new NotEqualsConditionTranslator(condition, mappingInfo);
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
			MappingInfo<?> mappingInfo) throws InvalidConditionException
	{
		SimpleField pf = getESField(condition, mappingInfo);
		switch (pf.getType()) {
			case GEO_POINT:
				/*
				 * NB we currently don't have any fields of type GeoPoint in our
				 * data model, so this is just for the sake of completeness:
				 */
				String msg = "IN operator not allowed for geo_point fields";
				throw new InvalidConditionException(msg);
			case GEO_SHAPE:
				if (condition.getValue() instanceof GeoJsonObject) {
					return new ShapeInShapeConditionTranslator(condition, mappingInfo);
				}
				if (isJson(condition.getValue())) {
					condition.setValue(getGeoJsonObject(condition.getValue()));
					return new ShapeInShapeConditionTranslator(condition, mappingInfo);
				}
				return new ShapeInLocalityConditionTranslator(condition, mappingInfo);
			default:
				return new InConditionTranslator(condition, mappingInfo);
		}
	}

	/*
	 * Whether or not we must assume that Condition.value is a JSON string, or
	 * at least not a geographical name like "Amsterdam". The assumption is that
	 * no geographical name starts with '{' and ends with '}', which seems safe.
	 */
	private static boolean isJson(Object val)
	{
		if (val.getClass() == String.class) {
			String s = val.toString().trim();
			return s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}';
		}
		return false;
	}

	private static GeoJsonObject getGeoJsonObject(Object val) throws InvalidConditionException
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

	private static void checkOperator(Condition condition, MappingInfo<?> mappingInfo)
			throws IllegalOperatorException
	{
		SimpleField field = getESField(condition, mappingInfo);
		if (!OperatorCheck.isOperatorAllowed(field, condition.getOperator())) {
			throw new IllegalOperatorException(condition);
		}
	}
}
