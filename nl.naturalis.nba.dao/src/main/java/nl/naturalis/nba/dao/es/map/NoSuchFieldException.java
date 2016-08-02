package nl.naturalis.nba.dao.es.map;

import nl.naturalis.nba.dao.es.exception.DaoException;


public class NoSuchFieldException extends DaoException {

	public NoSuchFieldException(String field)
	{
		super("No such field: \"" + field + "\"");
	}

}
