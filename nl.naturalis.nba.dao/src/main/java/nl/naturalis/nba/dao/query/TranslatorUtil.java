package nl.naturalis.nba.dao.query;

import static org.domainobject.util.ClassUtil.isA;
import static org.domainobject.util.ClassUtil.isNumber;

import java.util.Date;

import nl.naturalis.nba.api.query.ComparisonOperator;
import nl.naturalis.nba.api.query.Condition;
import nl.naturalis.nba.api.query.IllegalOperatorException;
import nl.naturalis.nba.api.query.InvalidConditionException;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.NoSuchFieldException;
import nl.naturalis.nba.common.es.map.PrimitiveField;

class TranslatorUtil {

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

	static String getNestedPath(Condition condition, MappingInfo mappingInfo)
	{
		PrimitiveField pf = getESField(condition, mappingInfo);
		return MappingInfo.getNestedPath(pf);
	}

	static PrimitiveField getESField(Condition condition, MappingInfo mappingInfo)
	{
		try {
			return (PrimitiveField) mappingInfo.getField(condition.getField());
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
		ComparisonOperator op = condition.getOperator();
		Class<?> type = condition.getValue().getClass();
		String fmt = "Search term has wrong type for operator %s: %s";
		return invalidConditionException(condition, fmt, op, type);
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

	static void ensureFieldIsDateOrNumber(Condition condition, MappingInfo mappingInfo)
			throws IllegalOperatorException
	{
		ESField field = null;
		try {
			field = mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			// Won't happend, already checked
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
