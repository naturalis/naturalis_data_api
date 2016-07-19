package nl.naturalis.nba.common.json;

public class JsonDeserializationException extends RuntimeException {

	public JsonDeserializationException(String message)
	{
		super(message);
	}

	public JsonDeserializationException(Throwable cause)
	{
		super(cause);
	}

}
