package nl.naturalis.nda.elasticsearch.load;

/**
 * @author Ayco Holleman
 * 
 * @deprecated Should be handled and logged by {@link Transformer}s without
 *             throwing an exception.
 *
 */
@SuppressWarnings("serial")
public class MissingDataException extends InvalidDataException {

	public MissingDataException()
	{
	}

	public MissingDataException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public MissingDataException(String message)
	{
		super(message);
	}

	public MissingDataException(Throwable cause)
	{
		super(cause);
	}

}
