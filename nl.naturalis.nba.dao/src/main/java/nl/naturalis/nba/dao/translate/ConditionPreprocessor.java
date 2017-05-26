package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.dao.NbaMetaDataDao.DEFAULT_DATE_FORMAT;
import static nl.naturalis.nba.dao.NbaMetaDataDao.DEFAULT_DATE_PATTERN;
import static nl.naturalis.nba.dao.NbaMetaDataDao.ACCEPTED_DATE_FORMATS;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.getESFieldType;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.invalidDataType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;

/**
 * Executes some always-necessary (operator-independent) preprocessing of a
 * {@link QueryCondition} before it is handed over to a
 * {@link ConditionTranslator}. Note that a {@link ConditionTranslator} may
 * further preprocess the condition in <i>its</i>
 * {@link ConditionTranslator#preprocess()} method before the condition is
 * finaly translated into an Elasticsearch query.
 * 
 * @author Ayco Holleman
 *
 */
class ConditionPreprocessor {

	private QueryCondition condition;
	private MappingInfo<?> mappingInfo;

	ConditionPreprocessor(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	void preprocessCondition() throws InvalidConditionException
	{
		switch (getESFieldType(condition, mappingInfo)) {
			case DATE:
				convertValueForDateField(condition);
				break;
			default:
				break;
		}
	}

	static void convertValueForDateField(QueryCondition condition) throws InvalidConditionException
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
			condition.setValue(convertValueForDateField(value, condition));
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
			return DEFAULT_DATE_FORMAT.format(d);
		}
		if (value instanceof Date) {
			return DEFAULT_DATE_FORMAT.format((Date) value);
		}
		if (value instanceof OffsetDateTime) {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);
			return ((OffsetDateTime) value).format(dtf);
		}
		throw invalidDataType(condition);
	}

	private static Date toDate(String value)
	{
		for (SimpleDateFormat sdf : ACCEPTED_DATE_FORMATS) {
			try {
				return sdf.parse(value);
			}
			catch (ParseException e) {}
		}
		return null;
	}
}
