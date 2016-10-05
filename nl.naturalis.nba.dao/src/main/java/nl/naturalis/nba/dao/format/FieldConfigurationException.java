package nl.naturalis.nba.dao.format;

public class FieldConfigurationException extends DataSetConfigurationException {

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
