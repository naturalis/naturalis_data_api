package nl.naturalis.nba.dao.format.dwca;

import nl.naturalis.nba.dao.exception.DaoException;

/**
 * Thrown if DwCA module encountered an unexpected error while generating a
 * DarwinCore archive.
 * 
 * @author Ayco Holleman
 *
 */
public class DwcaCreationException extends DaoException {
  
  private static final long serialVersionUID = 1L;

	public DwcaCreationException(String message)
	{
		super(message);
	}

	public DwcaCreationException(Throwable cause)
	{
		super(cause);
	}

	public DwcaCreationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
