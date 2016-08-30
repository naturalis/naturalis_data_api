package nl.naturalis.nba.dao.es.format;

class EntityConfigurationException extends Exception {

	EntityConfigurationException(String message)
	{
		super(message);
	}

	public EntityConfigurationException(Throwable t)
	{
		super(t);
	}
}