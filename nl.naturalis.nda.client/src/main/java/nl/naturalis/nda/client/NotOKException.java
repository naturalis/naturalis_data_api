package nl.naturalis.nda.client;

public class NotOKException extends Exception {

	private static final long serialVersionUID = -8246486578070786218L;


	public NotOKException()
	{
	}


	public NotOKException(String message)
	{
		super(message);
	}


	public NotOKException(Throwable cause)
	{
		super(cause);
	}


	public NotOKException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
