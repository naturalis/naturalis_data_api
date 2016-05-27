package nl.naturalis.nba.api.query;

import static java.lang.String.format;

public class IllegalOperatorException extends InvalidConditionException {

	public IllegalOperatorException(String field, ComparisonOperator operator)
	{
		super(format("Operator %s not allowed for field %s", operator, field));
	}

}
