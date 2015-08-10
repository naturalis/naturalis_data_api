package nl.naturalis.nda.service.rest.exception;

public class InvalidQueryException extends RuntimeException {

	private static final long serialVersionUID = -6813021595740639317L;

	public InvalidQueryException()
	{
		super();
	}

	public InvalidQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidQueryException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidQueryException(String message)
	{
		super(message);
	}

	public InvalidQueryException(Throwable cause)
	{
		super(cause);
	}

}
