package nl.naturalis.nda.elasticsearch.load;

/**
 * 
 * @deprecated Should be handled and logged by {@link Transformer}s without
 *             throwing an exception. Only here because NSR has not been
 *             converted to the ETL framework yet.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("serial")
public class InvalidDataException extends Exception {

	public InvalidDataException()
	{
		super();
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
