package nl.naturalis.nba.api.query;

/**
 * Throw when a {@link Condition query condition} contains an error.
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
