package nl.naturalis.nba.common.json;

public class JsonDeserializationException extends RuntimeException {

  private static final long serialVersionUID = -2529482491390845567L;

  public JsonDeserializationException(String message)
	{
		super(message);
	}

	public JsonDeserializationException(Throwable cause)
	{
		super(cause);
	}

}
