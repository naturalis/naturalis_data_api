package nl.naturalis.nba.elasticsearch.map;

public class MappingException extends RuntimeException {

	public MappingException()
	{
	}

	public MappingException(String message)
	{
		super(message);
	}

	public MappingException(Throwable cause)
	{
		super(cause);
	}

	public MappingException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
