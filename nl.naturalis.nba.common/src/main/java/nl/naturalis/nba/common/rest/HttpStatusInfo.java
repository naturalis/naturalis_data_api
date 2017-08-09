package nl.naturalis.nba.common.rest;

/**
 * Java bean encapsulating an HTTP status code and message.
 * 
 * @author Ayco Holleman
 *
 */
public class HttpStatusInfo {

	private int code;
	private String message;

	public HttpStatusInfo()
	{
	}

	public HttpStatusInfo(int code, String message)
	{
		this.code = code;
		this.message = message;
	}

	public int getCode()
	{
		return code;
	}

	public void setCode(int code)
	{
		this.code = code;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}
}