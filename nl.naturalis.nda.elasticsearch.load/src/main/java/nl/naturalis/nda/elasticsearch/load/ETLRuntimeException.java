package nl.naturalis.nda.elasticsearch.load;

public class ETLRuntimeException extends RuntimeException {

	public ETLRuntimeException()
	{
	}

	public ETLRuntimeException(String message)
	{
		super(message);
	}

	public ETLRuntimeException(Throwable cause)
	{
		super(cause);
	}

	public ETLRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
