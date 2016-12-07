package nl.naturalis.nba.api;

/**
 * Base class for all checked exceptions within the NBA.
 * 
 * @author Ayco Holleman
 *
 */
public class NbaException extends Exception {

	public NbaException()
	{
	}

	public NbaException(String message)
	{
		super(message);
	}

	public NbaException(Throwable cause)
	{
		super(cause);
	}

	public NbaException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
