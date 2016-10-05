package nl.naturalis.nba.dao.exception;

import nl.naturalis.nba.dao.DaoRegistry;

/**
 * Thrown during the instantiation of the central {@link DaoRegistry} object if
 * anything goes wrong while configuring the services it provides.
 * 
 * @author Ayco Holleman
 *
 */
public class InitializationException extends DaoException {

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
