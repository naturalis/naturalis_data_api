package nl.naturalis.nba.etl.normalize;

public class UnmappedValueException extends Exception {

  private static final long serialVersionUID = 1L;
  
	private static final String msg = "Value \"%s\" is not mapped to any canonical value for %s";

	public UnmappedValueException(String value, Class<? extends Enum<?>> type)
	{
		super(String.format(msg, value, type.getSimpleName()));
	}

}