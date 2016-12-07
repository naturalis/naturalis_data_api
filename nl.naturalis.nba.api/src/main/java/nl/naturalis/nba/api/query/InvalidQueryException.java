package nl.naturalis.nba.api.query;

import nl.naturalis.nba.api.NbaException;

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
