package nl.naturalis.nda.client;

public class ClientException extends RuntimeException {

	private static final long serialVersionUID = -3460791416182567992L;


	public ClientException()
	{
	}


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


	public ClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
