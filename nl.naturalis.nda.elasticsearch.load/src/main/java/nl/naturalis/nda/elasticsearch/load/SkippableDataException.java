package nl.naturalis.nda.elasticsearch.load;

@SuppressWarnings("serial")
public class SkippableDataException extends Exception {

	public SkippableDataException()
	{
		super();
	}


	public SkippableDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}


	public SkippableDataException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public SkippableDataException(String message)
	{
		super(message);
	}


	public SkippableDataException(Throwable cause)
	{
		super(cause);
	}

}
