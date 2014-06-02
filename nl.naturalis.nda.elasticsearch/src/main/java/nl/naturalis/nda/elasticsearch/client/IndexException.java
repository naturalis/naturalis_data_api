package nl.naturalis.nda.elasticsearch.client;

@SuppressWarnings("serial")
public class IndexException extends RuntimeException {
	
	public IndexException(String message)
	{
		super(message);
	}


	public IndexException(Throwable cause)
	{
		super(cause);
	}
	
}