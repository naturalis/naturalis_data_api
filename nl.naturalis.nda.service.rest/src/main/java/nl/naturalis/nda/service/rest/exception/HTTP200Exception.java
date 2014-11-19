package nl.naturalis.nda.service.rest.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

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
			
			Throwable rootCause = getCause();
			while (rootCause.getCause() != null) {
				rootCause = rootCause.getCause();
			}			
			
			HashMap<String, Object> exceptionInfo = new HashMap<>();
			info.put("exception", exceptionInfo);
			exceptionInfo.put("message", rootCause.getMessage());
			exceptionInfo.put("root cause", rootCause.getClass().getName());
			if (rootCause != getCause()) {
				exceptionInfo.put("cause", getCause().getClass().getName());
			}
			StackTraceElement[] stackTraceElements = rootCause.getStackTrace();
			List<String> trace = new ArrayList<String>(stackTraceElements.length);
			for (StackTraceElement e : stackTraceElements) {
				StringBuilder sb = new StringBuilder(100);
				sb.append("at ");
				sb.append(e.getClassName()).append('.').append(e.getMethodName());
				sb.append('(').append(e.getFileName()).append(':').append(e.getLineNumber()).append(')');
				trace.add(sb.toString());
			}
			exceptionInfo.put("stackTrace", trace);
		}
		return info;
	}

}
