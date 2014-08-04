package nl.naturalis.nda.elasticsearch.load;

@SuppressWarnings("serial")
public class HarvestException extends RuntimeException {

	public HarvestException()
	{
	}


	public HarvestException(String message)
	{
		super(message);
	}


	public HarvestException(Throwable cause)
	{
		super(cause);
	}


	public HarvestException(String message, Throwable cause)
	{
		super(message, cause);
	}


	public HarvestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
