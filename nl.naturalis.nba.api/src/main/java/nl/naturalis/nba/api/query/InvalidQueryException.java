package nl.naturalis.nba.api.query;

/**
 * Thrown when {@link QuerySpec query specification} contains an error.
 * 
 * @author Ayco Holleman
 *
 */
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
