package nl.naturalis.nba.client;

public class ClientException extends RuntimeException {

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
