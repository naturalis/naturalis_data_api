package nl.naturalis.nba.dao.format;

import nl.naturalis.nba.api.NbaException;

public class DataSetWriteException extends NbaException {

	public DataSetWriteException(String message)
	{
		super(message);
	}

	public DataSetWriteException(Throwable t)
	{
		super(t);
	}

}
