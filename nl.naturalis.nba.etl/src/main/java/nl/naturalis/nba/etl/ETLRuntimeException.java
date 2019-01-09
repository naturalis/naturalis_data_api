package nl.naturalis.nba.etl;

/**
 * Generic runtime exception thrown throughout this library in case of
 * unrecoverable errors.
 * 
 * @author Ayco Holleman
 *
 */
public class ETLRuntimeException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ETLRuntimeException()
	{
	}

	public ETLRuntimeException(String message)
	{
		super(message);
	}

	public ETLRuntimeException(Throwable cause)
	{
		super(cause);
	}

	public ETLRuntimeException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
