package nl.naturalis.nba.dao.es.format;

public class DataSetConfigurationException extends Exception {

	DataSetConfigurationException(String message)
	{
		super(message);
	}

	public DataSetConfigurationException(Throwable t)
	{
		super(t);
	}
}