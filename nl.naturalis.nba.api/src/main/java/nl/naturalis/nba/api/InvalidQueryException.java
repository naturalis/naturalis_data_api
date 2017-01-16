package nl.naturalis.nba.api;

/**
 * Thrown when {@link QuerySpec query specification} contains an error.
 * 
 * @author Ayco Holleman
 *
 */
public class InvalidQueryException extends NbaException {

	public InvalidQueryException(String message)
	{
		super(message);
	}

	public InvalidQueryException(Throwable cause)
	{
		super(cause);
	}

}
