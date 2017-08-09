package nl.naturalis.nba.common.rest;

/**
 * Java bean summarizing the result of calling one of the NBA's REST services.
 * 
 * @author Ayco Holleman
 *
 */
public class ServerInfo {

	private String requestUri;
	private HttpStatusInfo httpStatus;
	private ExceptionInfo exception;

	public String getRequestUri()
	{
		return requestUri;
	}

	public void setRequestUri(String requestUri)
	{
		this.requestUri = requestUri;
	}

	public HttpStatusInfo getHttpStatus()
	{
		return httpStatus;
	}

	public void setHttpStatus(HttpStatusInfo httpStatus)
	{
		this.httpStatus = httpStatus;
	}

	public ExceptionInfo getException()
	{
		return exception;
	}

	public void setException(ExceptionInfo exception)
	{
		this.exception = exception;
	}

}
