package nl.naturalis.nba.dao.es.format;

public class NoSuchDataSetException extends DataSetConfigurationException {

	public NoSuchDataSetException(String message)
	{
		super(message);
	}

	public NoSuchDataSetException(Throwable t)
	{
		super(t);
	}

}
