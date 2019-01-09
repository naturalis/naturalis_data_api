package nl.naturalis.nba.dao.exception;

/**
 * Base class for runtime exceptions emanating from the data access module.
 * 
 * @author Ayco Holleman
 *
 */
public class DaoException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  
	public DaoException(String message)
	{
		super(message);
	}

	public DaoException(Throwable cause)
	{
		super(cause);
	}

	public DaoException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
