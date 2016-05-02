package nl.naturalis.nba.api.query;

public class InvalidConditionException extends InvalidQueryException {

	public InvalidConditionException(String message)
	{
		super(message);
	}

	public InvalidConditionException(Throwable cause)
	{
		super(cause);
	}

}
