package nl.naturalis.nba.etl;


public class ConnectionFailureException extends InitializationException {
	
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
