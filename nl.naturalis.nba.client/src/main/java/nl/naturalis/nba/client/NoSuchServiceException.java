package nl.naturalis.nba.client;

/**
 * Thrown when a client attempts to call a non-existing NBA service. This means
 * that the version of client library being used in not up-to-date.
 * 
 * @author Ayco Holleman
 *
 */
public class NoSuchServiceException extends ClientException {

  private static final long serialVersionUID = 3164188597698898700L;

  public NoSuchServiceException()
	{
		super("The client specified a non-existent NBA service endpoint. This is a bug.");
	}

}
