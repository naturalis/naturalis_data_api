package nl.naturalis.nda.export;

public class ExportException extends RuntimeException {

	private static final long serialVersionUID = 6591392106215364101L;


	public ExportException()
	{
	}


	public ExportException(String arg0)
	{
		super(arg0);
	}


	public ExportException(Throwable arg0)
	{
		super(arg0);
	}


	public ExportException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}


	public ExportException(String arg0, Throwable arg1, boolean arg2, boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
	}

}
