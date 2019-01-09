package nl.naturalis.nba.dao.format;

public class FieldConfigurationException extends DataSetConfigurationException {

  private static final long serialVersionUID = 1L;
  
	private String field;

	public FieldConfigurationException(String field, String message)
	{
		super(message);
		this.field = field;
	}

	String getField()
	{
		return field;
	}

}
