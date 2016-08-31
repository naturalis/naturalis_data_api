package nl.naturalis.nba.dao.es.format;

public class EntityConfigurationException extends DataSetConfigurationException {

	public EntityConfigurationException(String message)
	{
		super(message);
	}

	public EntityConfigurationException(Throwable t)
	{
		super(t);
	}
}