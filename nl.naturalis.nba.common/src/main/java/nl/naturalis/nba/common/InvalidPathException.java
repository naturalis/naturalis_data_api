package nl.naturalis.nba.common;

public class InvalidPathException extends RuntimeException {

  private static final long serialVersionUID = 2846174292210652255L;

  public InvalidPathException(String message)
	{
		super(message);
	}

}
