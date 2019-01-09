package nl.naturalis.nba.dao.exception;

/**
 * Thrown if anything goes wrong in the NBA start-up phase.
 * 
 * @author Ayco Holleman
 *
 */
public class InitializationException extends DaoException {

  private static final long serialVersionUID = 1L;
  
	/**
	 * @param message
	 */
	public InitializationException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public InitializationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InitializationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
