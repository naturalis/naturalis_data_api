package nl.naturalis.nba.dao.exception;

public class ConnectionFailureException extends DaoException {

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
