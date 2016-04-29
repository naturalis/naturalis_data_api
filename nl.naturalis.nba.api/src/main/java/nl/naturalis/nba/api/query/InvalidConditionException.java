package nl.naturalis.nba.api.query;


public class InvalidConditionException extends Exception {

	public InvalidConditionException(String message)
	{
		super(message);
	}

	public InvalidConditionException(Throwable cause)
	{
		super(cause);
	}


}
