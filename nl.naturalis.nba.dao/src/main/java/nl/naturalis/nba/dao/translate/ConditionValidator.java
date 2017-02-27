package nl.naturalis.nba.dao.translate;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.IllegalOperatorException;
import nl.naturalis.nba.api.InvalidConditionException;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.Path;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.common.es.map.ESField;
import nl.naturalis.nba.common.es.map.MappingInfo;
import nl.naturalis.nba.common.es.map.SimpleField;

/**
 * Ensures the a {@link QueryCondition} specifies an existing field and that it
 * is a primitive field (not an object).
 * 
 * @author Ayco Holleman
 *
 */
class ConditionValidator {

	private QueryCondition condition;
	private MappingInfo<?> mappingInfo;

	ConditionValidator(QueryCondition condition, MappingInfo<?> mappingInfo)
	{
		this.condition = condition;
		this.mappingInfo = mappingInfo;
	}

	void validateCondition() throws InvalidConditionException
	{
		Path path = condition.getField();
		ComparisonOperator operator = condition.getOperator();
		if (path == null) {
			String msg = "Missing field in search condition";
			throw new InvalidConditionException(msg);
		}
		if (operator == null) {
			String msg = "Missing operator in search condition";
			throw new InvalidConditionException(msg);
		}
		ESField field;
		try {
			field = mappingInfo.getField(path);
		}
		catch (NoSuchFieldException e) {
			throw new InvalidConditionException(e.getMessage());
		}
		if (!(field instanceof SimpleField)) {
			String fmt = "Field %s cannot be queried: field is an object";
			String msg = String.format(fmt, path);
			throw new InvalidConditionException(msg);
		}
		SimpleField sf = (SimpleField) field;
		if (sf.getIndex() == Boolean.FALSE) {
			String fmt = "Field %s cannot be queried: field is not indexed";
			String msg = String.format(fmt, path);
			throw new InvalidConditionException(msg);
		}
		if (!OperatorValidator.isOperatorAllowed(sf, operator)) {
			throw new IllegalOperatorException(path, operator);
		}
	}

}
