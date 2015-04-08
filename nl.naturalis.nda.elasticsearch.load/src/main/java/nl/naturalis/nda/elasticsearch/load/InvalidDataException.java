package nl.naturalis.nda.elasticsearch.load;

@SuppressWarnings("serial")
public class InvalidDataException extends Exception {

	public InvalidDataException()
	{
		super();
	}


	public InvalidDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public InvalidDataException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public InvalidDataException(String message)
	{
		super(message);
	}


	public InvalidDataException(Throwable cause)
	{
		super(cause);
	}

}
