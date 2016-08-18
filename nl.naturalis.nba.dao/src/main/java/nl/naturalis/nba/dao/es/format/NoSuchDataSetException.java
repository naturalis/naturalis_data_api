package nl.naturalis.nba.dao.es.format;

import nl.naturalis.nba.dao.es.exception.DaoException;


public class NoSuchDataSetException extends DaoException {

	public NoSuchDataSetException(String message)
	{
		super(message);
	}

	public NoSuchDataSetException(Throwable cause)
	{
		super(cause);
	}

	public NoSuchDataSetException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
