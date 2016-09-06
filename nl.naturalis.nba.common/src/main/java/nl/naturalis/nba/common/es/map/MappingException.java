package nl.naturalis.nba.common.es.map;

public class MappingException extends RuntimeException {

	public MappingException(String message)
	{
		super(message);
	}

	public MappingException(Throwable cause)
	{
		super(cause);
	}

}
