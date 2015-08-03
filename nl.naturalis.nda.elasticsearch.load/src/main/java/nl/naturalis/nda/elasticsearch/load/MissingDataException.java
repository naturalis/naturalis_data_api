package nl.naturalis.nda.elasticsearch.load;

/**
 * @author Ayco Holleman
 * @created Jul 29, 2015
 *
 */
@SuppressWarnings("serial")
public class MissingDataException extends InvalidDataException {

	public MissingDataException()
	{
	}


	public MissingDataException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public MissingDataException(String message)
	{
		super(message);
	}


	public MissingDataException(Throwable cause)
	{
		super(cause);
	}

}
