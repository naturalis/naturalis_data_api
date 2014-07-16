package nl.naturalis.nda.elasticsearch.dao.exception;

@SuppressWarnings("serial")
public class InvalidQueryException extends RuntimeException {

	public InvalidQueryException()
	{
	}


	public InvalidQueryException(String message)
	{
		super(message);
	}


	public InvalidQueryException(Throwable cause)
	{
		super(cause);
	}


	public InvalidQueryException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public InvalidQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
