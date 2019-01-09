package nl.naturalis.nba.client;

/**
 * A {@link RuntimeException} thrown when error conditions arise in client-side
 * code.
 * 
 * @author Ayco Holleman
 *
 */
public class ClientException extends RuntimeException {

  private static final long serialVersionUID = 1717810212157737191L;

  public ClientException(String message)
	{
		super(message);
	}

	public ClientException(Throwable cause)
	{
		super(cause);
	}

	public ClientException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
