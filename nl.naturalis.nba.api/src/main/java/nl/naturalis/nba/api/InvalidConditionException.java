package nl.naturalis.nba.api;

/**
 * Throw when a {@link QueryCondition query condition} contains an error.
 * 
 * @author Ayco Holleman
 *
 */
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
