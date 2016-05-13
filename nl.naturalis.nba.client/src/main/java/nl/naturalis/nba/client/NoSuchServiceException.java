package nl.naturalis.nba.client;

public class NoSuchServiceException extends ClientException {

	public NoSuchServiceException()
	{
		super("The client specified a non-existent NBA service endpoint. This is a bug.");
	}

}
