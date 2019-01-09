package nl.naturalis.nba.common.es.map;

public class MappingException extends RuntimeException {

  private static final long serialVersionUID = 8625874695224740933L;

  public MappingException(String message)
	{
		super(message);
	}

	public MappingException(Throwable cause)
	{
		super(cause);
	}

}
