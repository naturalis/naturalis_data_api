package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.utils.ClassUtil.isA;
import static nl.naturalis.nba.utils.ClassUtil.isNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;

class TranslatorUtil {

	private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	private static final SimpleDateFormat SDF0 = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
	private static final SimpleDateFormat SDF1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat SDF2 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat SDF3 = new SimpleDateFormat("yyyy");

	private static final SimpleDateFormat[] acceptedDateFormats = new SimpleDateFormat[] { SDF0,
			SDF1, SDF2, SDF3 };

	static String getNestedPath(Path path, MappingInfo<?> mappingInfo)
	{
		SimpleField sf = getESField(path, mappingInfo);
		return MappingInfo.getNestedPath(sf);
	}

	static ESDataType getESFieldType(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		return getESField(condition, mappingInfo).getType();
	}

	static SimpleField getESField(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		try {
			return (SimpleField) mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Assumption: path already validated; won't happen
			assert (false);
			return null;
		}
	}

	static SimpleField getESField(Path path, MappingInfo<?> mappingInfo)
	{
		try {
			return (SimpleField) mappingInfo.getField(path);
		}
		catch (NoSuchFieldException e) {
			// Assumption: path already validated; won't happen
			assert (false);
			return null;
		}
	}

	static void ensureValueIsNotNull(QueryCondition condition) throws InvalidConditionException
	{
		if (condition.getValue() == null) {
			String fmt = "Search value must not be null when using operator %s";
			throw new InvalidConditionException(condition, fmt, condition.getOperator());
		}
	}

	static InvalidConditionException invalidDataType(QueryCondition condition)
	{
		String type = condition.getValue().getClass().getName();
		String fmt = "Search value has invalid data type: %s";
		return new InvalidConditionException(condition, fmt, type);
	}

	static void ensureValueIsString(QueryCondition condition) throws InvalidConditionException
	{
		if (condition.getValue().getClass() != String.class) {
			throw invalidDataType(condition);
		}
	}

	static void ensureValueIsDateOrNumber(QueryCondition condition)
			throws InvalidConditionException
	{
		if (!isNumber(condition.getValue()) && !isA(condition.getValue(), Date.class)) {
			throw invalidDataType(condition);
		}
	}

	static void convertValueForDateField(QueryCondition condition) throws InvalidConditionException
	{
		Object value = condition.getValue();
		if (value != null) {
			condition.setValue(convertValueForDateField(value, condition));
		}
	}

	static void convertValuesForDateField(QueryCondition condition)
			throws InvalidConditionException
	{
		Object value = condition.getValue();
		if (value == null) {
			return;
		}
		if (value.getClass().isArray()) {
			Object[] elems = (Object[]) value;
			String[] dates = new String[elems.length];
			int i = 0;
			for (Object elem : elems) {
				dates[i++] = convertValueForDateField(elem, condition);
			}
			condition.setValue(dates);
		}
		else if (value instanceof Collection) {
			Collection<?> elems = (Collection<?>) value;
			String[] dates = new String[elems.size()];
			int i = 0;
			for (Object elem : elems) {
				dates[i++] = convertValueForDateField(elem, condition);
			}
			condition.setValue(dates);
		}
		else {
			String[] dates = new String[] { convertValueForDateField(value, condition) };
			condition.setValue(dates);
		}
	}

	private static String convertValueForDateField(Object value, QueryCondition condition)
			throws InvalidConditionException
	{
		if (value instanceof CharSequence) {
			if (value.toString().isEmpty()) {
				return null;
			}
			Date d = toDate(value.toString());
			if (d == null) {
				String fmt = "Invalid date for query condition on field %s: %s";
				String msg = String.format(fmt, condition.getField());
				throw new InvalidConditionException(msg);
			}
			return SDF0.format(d);
		}
		if (value instanceof Date) {
			return SDF0.format((Date) value);
		}
		if (value instanceof OffsetDateTime) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
			return ((OffsetDateTime) value).format(dtf);
		}
		throw invalidDataType(condition);
	}

	private static Date toDate(String value)
	{
		for (SimpleDateFormat sdf : acceptedDateFormats) {
			try {
				return sdf.parse(value);
			}
			catch (ParseException e) {}
		}
		return null;
	}
}
