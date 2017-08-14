package nl.naturalis.nba.dao.translate;

import static nl.naturalis.nba.dao.translate.TranslatorUtil.getESFieldType;
import static nl.naturalis.nba.dao.translate.TranslatorUtil.invalidDataType;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Date;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.dao.util.es.ESDateInput;

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
		ESDateInput dateInput = new ESDateInput();
		if (value instanceof CharSequence) {
			if (value.toString().isEmpty()) {
				return null;
			}
			String esDate = dateInput.toESFormat(value.toString());
			if (esDate == null) {
				String fmt = "Invalid date for query condition on field %s: %s";
				String msg = String.format(fmt, condition.getField(), value);
				throw new InvalidConditionException(msg);
			}
			return esDate;
		}
		if (value instanceof Date) {
			return dateInput.toESFormat((Date) value);
		}
		if (value instanceof OffsetDateTime) {
			return dateInput.toESFormat((OffsetDateTime) value);
		}
		throw invalidDataType(condition);
	}

}
