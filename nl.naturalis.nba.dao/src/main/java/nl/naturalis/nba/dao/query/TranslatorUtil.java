package nl.naturalis.nba.dao.query;

import static nl.naturalis.nba.utils.ClassUtil.isA;
import static nl.naturalis.nba.utils.ClassUtil.isNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.SimpleField;

class TranslatorUtil {

	private static final SimpleDateFormat SDF0 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy/MM/dd");
	private static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final SimpleDateFormat SDF4 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	private static final SimpleDateFormat[] acceptedDateFormats = new SimpleDateFormat[] { SDF0,
			SDF1, SDF2, SDF3, SDF4 };

	static InvalidConditionException invalidConditionException(Condition condition, String msg,
			Object... msgArgs)
	{
		StringBuilder sb = new StringBuilder(100);
		sb.append("Invalid query condition for field ");
		sb.append(condition.getField());
		sb.append(". ");
		sb.append(String.format(msg, msgArgs));
		return new InvalidConditionException(sb.toString());
	}

	static String getNestedPath(Condition condition, MappingInfo<?> mappingInfo)
	{
		SimpleField pf = getESField(condition, mappingInfo);
		return MappingInfo.getNestedPath(pf);
	}

	static SimpleField getESField(Condition condition, MappingInfo<?> mappingInfo)
	{
		try {
			return (SimpleField) mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happen because already checked in ConditionTranslatorFactory
			return null;
		}
	}

	static InvalidConditionException searchTermMustNotBeNull(Condition condition)
	{
		String fmt = "Search term must not be null when using operator %s";
		return invalidConditionException(condition, fmt, condition.getOperator());
	}

	static InvalidConditionException searchTermHasWrongType(Condition condition)
	{
		String type = condition.getValue().getClass().getName();
		String fmt = "Search term has wrong type for query condition on field %s: %s";
		return invalidConditionException(condition, fmt, condition.getField(), type);
	}

	static void ensureValueIsNotNull(Condition condition) throws InvalidConditionException
	{
		if (condition.getValue() == null) {
			String fmt = "Search term must not be null when using operator %s";
			throw invalidConditionException(condition, fmt, condition.getOperator());
		}
	}

	static void ensureValueIsString(Condition condition) throws InvalidConditionException
	{
		if (condition.getValue().getClass() != String.class) {
			throw searchTermHasWrongType(condition);
		}
	}

	static void ensureValueIsDateOrNumber(Condition condition) throws InvalidConditionException
	{
		if (!isNumber(condition.getValue()) && !isA(condition.getValue(), Date.class)) {
			throw searchTermHasWrongType(condition);
		}
	}

	static Date asDate(String value)
	{
		for (SimpleDateFormat sdf : acceptedDateFormats) {
			try {
				return sdf.parse(value);
			}
			catch (ParseException e) {}
		}
		return null;
	}

	static void ensureFieldIsDateOrNumber(Condition condition, MappingInfo<?> mappingInfo)
			throws IllegalOperatorException
	{
		ESField field = null;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happen because already checked in ConditionTranslatorFactory
			assert (false);
		}
		switch (field.getType()) {
			case BYTE:
			case DATE:
			case DOUBLE:
			case FLOAT:
			case INTEGER:
			case LONG:
			case SHORT:
				break;
			default:
				throw new IllegalOperatorException(condition);
		}
	}
}
