package nl.naturalis.nda.elasticsearch.client;

/**
 * Generic runtime exception thrown by the index managers.
 * 
 * @author Ayco Holleman
 *
 */
@SuppressWarnings("serial")
public class IndexManagerException extends RuntimeException {

	public IndexManagerException(String message)
	{
		super(message);
	}

	public IndexManagerException(Throwable cause)
	{
		super(cause);
	}

}