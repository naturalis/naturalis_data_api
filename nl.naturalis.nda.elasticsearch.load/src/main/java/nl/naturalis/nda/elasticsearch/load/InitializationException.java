package nl.naturalis.nda.elasticsearch.load;

/**
 * @author Ayco Holleman
 *
 */
public class InitializationException extends RuntimeException {

	/**
	 * @param message
	 */
	public InitializationException(String message)
	{
		super(message);
	}


	/**
	 * @param cause
	 */
	public InitializationException(Throwable cause)
	{
		super(cause);
	}


	/**
	 * @param message
	 * @param cause
	 */
	public InitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
