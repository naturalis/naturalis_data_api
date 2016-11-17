package nl.naturalis.nba.utils.http;

@SuppressWarnings("serial")
public class SimpleHttpException extends RuntimeException {

	public SimpleHttpException(String message)
	{
		super(message);
	}


	public SimpleHttpException(Throwable cause)
	{
		super(cause);
	}


	public SimpleHttpException(String message, Throwable cause)
	{
		super(message, cause);
	}


}
