package nl.naturalis.nba.dao.translate;

import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESDataType;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;

class TranslatorUtil {

	/**
	 * Whether or not the specified condition must be interpreted as the ALWAYS
	 * TRUE condition (akin to SQL's WHERE true).
	 * 
	 * @param condition
	 * @return
	 */
	static boolean isTrueCondition(QueryCondition condition)
	{
		return condition.getField() == null && condition.getOperator() == null
				&& condition.getValue() == null;
	}

	static String getNestedPath(Path path, MappingInfo<?> mappingInfo)
	{
		SimpleField sf = getESField(path, mappingInfo);
		return MappingInfo.getNestedPath(sf);
	}

	static ESDataType getESFieldType(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		return getESField(condition, mappingInfo).getType();
	}

	/**
	 * Returns a {@link SimpleField} instance corresponding to the condition's
	 * path, suppressing the NoSuchFieldException under the assumption that the
	 * path has already been validated.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 */
	static SimpleField getESField(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		try {
			return (SimpleField) mappingInfo.getField(condition.getField());
		}
		catch (NoSuchFieldException e) {
			assert (false);
			return null;
		}
	}

	/**
	 * Returns a {@link SimpleField} instance corresponding to the specified
	 * path, suppressing the NoSuchFieldException under the assumption that the
	 * path has already been validated.
	 * 
	 * @param condition
	 * @param mappingInfo
	 * @return
	 */
	static SimpleField getESField(Path path, MappingInfo<?> mappingInfo)
	{
		try {
			return (SimpleField) mappingInfo.getField(path);
		}
		catch (NoSuchFieldException e) {
			/*
			 * Assumption when calling this method: path already validated;
			 * won't happen
			 */
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


}
