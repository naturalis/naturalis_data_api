package nl.naturalis.nba.api;

import static java.lang.String.format;

/**
 * Thrown when a {@link ComparisonOperator query operator} is used for a field
 * that does not does not support that operator (e&#46;g&#46; when you use
 * {@link ComparisonOperator#NOT_LIKE NOT_LIKE} on a number field).
 * 
 * @author Ayco Holleman
 *
 */
public class IllegalOperatorException extends InvalidConditionException {

	public IllegalOperatorException(QueryCondition condition)
	{
		this(condition.getField(), condition.getOperator());
	}

	public IllegalOperatorException(String field, ComparisonOperator operator)
	{
		super(format("Operator %s not allowed for field %s", operator, field));
	}

}
