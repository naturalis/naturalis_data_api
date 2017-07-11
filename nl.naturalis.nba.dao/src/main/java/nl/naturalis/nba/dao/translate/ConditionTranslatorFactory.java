package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.common.json.JsonUtil.deserialize;
import static nl.naturalis.nba.dao.DaoUtil.getLogger;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getESField;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getESFieldType;

import org.apache.logging.log4j.Logger;
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;
import nl.naturalis.nba.common.json.JsonDeserializationException;
import nl.naturalis.nba.dao.DocumentType;

/**
 * A factory for {@link ConditionTranslator} instances. Creates a
 * {@link ConditionTranslator} for the {@link QueryCondition} passed to its
 * factory methods.
 * 
 * @author Ayco Holleman
 *
 */
public class ConditionTranslatorFactory {

	private static final Logger logger = getLogger(ConditionTranslatorFactory.class);

	private ConditionTranslatorFactory()
	{
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified
	 * {@link QueryCondition condition} and the specified {@link DocumentType
	 * document type}.
	 * 
	 * @param condition
	 * @param type
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(QueryCondition condition, DocumentType<?> type)
			throws InvalidConditionException
	{
		return getTranslator(condition, new MappingInfo<>(type.getMapping()));
	}

	/**
	 * Returns a {@link ConditionTranslator} for the specified
	 * {@link QueryCondition condition} and the specified {@link MappingInfo}
	 * object.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 * @throws InvalidConditionException
	 */
	public static ConditionTranslator getTranslator(QueryCondition condition,
			MappingInfo<?> mappingInfo) throws InvalidConditionException
	{
		if (TranslatorUtil.isTrueCondition(condition)) {
			return new TrueConditionTranslator(condition, mappingInfo);
		}
		if (TranslatorUtil.isFalseCondition(condition)) {
			return new FalseConditionTranslator(condition, mappingInfo);
		}
		ConditionValidator validator = new ConditionValidator(condition, mappingInfo);
		validator.validateCondition();
		ConditionPreprocessor preprocessor = new ConditionPreprocessor(condition, mappingInfo);
		preprocessor.preprocessCondition();
		ConditionTranslator translator = null;
		switch (condition.getOperator()) {
			case EQUALS:
				if (condition.getValue() == null)
					translator = new IsNullConditionTranslator(condition, mappingInfo);
				else
					translator = new EqualsConditionTranslator(condition, mappingInfo);
				break;
			case NOT_EQUALS:
				if (condition.getValue() == null)
					translator = new IsNotNullConditionTranslator(condition, mappingInfo);
				else
					translator = new NotEqualsConditionTranslator(condition, mappingInfo);
				break;
			case EQUALS_IC:
				if (condition.getValue() == null)
					translator = new IsNullConditionTranslator(condition, mappingInfo);
				else if (getESFieldType(condition, mappingInfo) == ESDataType.KEYWORD)
					translator = new EqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
				else
					translator = new EqualsConditionTranslator(condition, mappingInfo);
				/*
				 * Last option is a gesture to the client. The
				 * equals-ignore-case comparator only makes sense for strings.
				 * So for numbers, dates, etc. we tacitly submit the condition
				 * to the EqualsConditionTranslator instead of the
				 * EqualsIgnoreCaseConditionTranslator. This also allows for a
				 * high-level instruction like "Do a case-insensitive query
				 * where applicable". This is what the _ignoreCase query
				 * parameter does. See the HttpQuerySpecBuilder class in the
				 * rest module.
				 */
				break;
			case NOT_EQUALS_IC:
				if (condition.getValue() == null)
					translator = new IsNotNullConditionTranslator(condition, mappingInfo);
				else if (getESFieldType(condition, mappingInfo) == ESDataType.KEYWORD)
					translator = new NotEqualsIgnoreCaseConditionTranslator(condition, mappingInfo);
				else
					translator = new NotEqualsConditionTranslator(condition, mappingInfo);
				break;
			case GT:
				translator = new GTConditionTranslator(condition, mappingInfo);
				break;
			case GTE:
				translator = new GTEConditionTranslator(condition, mappingInfo);
				break;
			case LT:
				translator = new LTConditionTranslator(condition, mappingInfo);
				break;
			case LTE:
				translator = new LTEConditionTranslator(condition, mappingInfo);
				break;
			case BETWEEN:
			case NOT_BETWEEN:
				translator = new BetweenConditionTranslator(condition, mappingInfo);
				break;
			case LIKE:
			case NOT_LIKE:
				translator = new LikeConditionTranslator(condition, mappingInfo);
				break;
			case IN:
			case NOT_IN:
				translator = getInConditionTranslator(condition, mappingInfo);
				break;
			case MATCHES:
			case NOT_MATCHES:
				translator = new MatchesConditionTranslator(condition, mappingInfo);
				break;
		}
		if (logger.isDebugEnabled()) {
			String s = translator.getClass().getSimpleName();
			String t = condition.getField().toString();
			logger.debug("Instantiating {} for condition on field {}", s, t);
		}
		return translator;
	}

	private static ConditionTranslator getInConditionTranslator(QueryCondition condition,
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

}
