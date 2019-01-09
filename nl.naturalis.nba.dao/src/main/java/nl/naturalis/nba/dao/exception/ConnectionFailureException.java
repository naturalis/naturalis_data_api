package nl.naturalis.nba.dao.exception;

/**
 * Thrown when failing to connect to Elasticsearch.
 * 
 * @author Ayco Holleman
 *
 */
public class ConnectionFailureException extends DaoException {

  private static final long serialVersionUID = 1L;

  private static final String PREFIX = "Error while connecting to Elasticsearch cluster. ";

	public ConnectionFailureException(String message)
	{
		super(PREFIX + message);
	}

	public ConnectionFailureException(Throwable cause)
	{
		super(cause);
	}

}
