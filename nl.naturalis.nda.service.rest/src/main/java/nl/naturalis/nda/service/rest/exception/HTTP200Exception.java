package nl.naturalis.nda.service.rest.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

@SuppressWarnings("serial")
public class HTTP200Exception extends RuntimeException {

	private final Status actualStatus;
	private final UriInfo request;


	public HTTP200Exception(UriInfo request, Throwable cause, Status actualStatus)
	{
		super(cause);
		this.actualStatus = actualStatus;
		this.request = request;
	}


	public HTTP200Exception(UriInfo request, Throwable cause)
	{
		super(cause);
		this.actualStatus = Status.INTERNAL_SERVER_ERROR;
		this.request = request;
	}


	public HTTP200Exception(UriInfo request, Status actualStatus)
	{
		this.actualStatus = actualStatus;
		this.request = request;
	}


	public Map<String, Object> getInfo()
	{
		Map<String, Object> info = new HashMap<>();
		info.put("request", request.getRequestUri().toString());

		HashMap<String, Object> httpStatusInfo = new HashMap<>();
		info.put("httpStatus", httpStatusInfo);
		httpStatusInfo.put("code", actualStatus.getStatusCode());
		httpStatusInfo.put("message", actualStatus.toString());

		if (getCause() != null) {
			HashMap<String, Object> exceptionInfo = new HashMap<>();
			info.put("exception", exceptionInfo);
			exceptionInfo.put("message", getCause().getMessage());
			StringWriter sw = new StringWriter(255);
			PrintWriter pw = new PrintWriter(sw);
			getCause().printStackTrace(pw);
			exceptionInfo.put("stackTrace", sw.toString());
		}
		return info;
	}

}
