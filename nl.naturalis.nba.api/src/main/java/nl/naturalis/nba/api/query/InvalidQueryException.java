package nl.naturalis.nba.api.query;


public class InvalidQueryException extends Exception {

	public InvalidQueryException(String message)
	{
		super(message);
	}

	public InvalidQueryException(Throwable cause)
	{
		super(cause);
	}

}
