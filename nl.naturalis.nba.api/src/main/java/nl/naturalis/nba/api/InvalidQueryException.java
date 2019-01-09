package nl.naturalis.nba.api;

/**
 * Thrown when {@link QuerySpec query specification} contains an error.
 * 
 * @author Ayco Holleman
 *
 */
public class InvalidQueryException extends NbaException {

  private static final long serialVersionUID = -5045911687211902021L;

  public InvalidQueryException(String message)
	{
		super(message);
	}

	public InvalidQueryException(Throwable cause)
	{
		super(cause);
	}

}
