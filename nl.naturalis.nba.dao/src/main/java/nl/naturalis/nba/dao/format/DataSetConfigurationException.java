package nl.naturalis.nba.dao.format;

public class DataSetConfigurationException extends Exception {
  
  private static final long serialVersionUID = 1L;

	public DataSetConfigurationException(String message)
	{
		super(message);
	}

	public DataSetConfigurationException(Throwable cause)
	{
		super(cause);
	}

}