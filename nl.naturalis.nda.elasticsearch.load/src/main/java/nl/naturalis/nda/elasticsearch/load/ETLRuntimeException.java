package nl.naturalis.nda.elasticsearch.load;

/**
 * Generic runtime exception thrown through this library in case of
 * unrecoverable errors.
 * 
 * @author Ayco Holleman
 *
 */
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
