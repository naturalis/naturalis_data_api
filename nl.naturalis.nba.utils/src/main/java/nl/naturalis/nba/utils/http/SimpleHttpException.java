package nl.naturalis.nba.utils.http;

public class SimpleHttpException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;

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
