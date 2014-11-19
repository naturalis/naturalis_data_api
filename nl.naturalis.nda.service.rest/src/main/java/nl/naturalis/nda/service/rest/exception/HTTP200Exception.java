package nl.naturalis.nda.service.rest.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.search.QueryParams;

/**
 * A {@code HTTP200Exception} is thrown if the intent is to provide error
 * information within the response body. This can be useful for debugging
 * purposes. The exception is used within a workflow in which the HTTP request
 * seemingly completes normally (status 200 - OK), while the "actual status"
 * (most likely 500 - INTERNAL SERVER ERROR) and other error information can be
 * retrieved from the {@code HTTP200Exception} object and serialized to the
 * response body.
 * 
 * @author ayco_holleman
 * 
 */
@SuppressWarnings("serial")
public class HTTP200Exception extends RuntimeException {

	private final Status actualStatus;
	private final UriInfo request;
	private final MultivaluedMap<String, String> formParams;


	public HTTP200Exception(UriInfo request, Status actualStatus)
	{
		this(request, null, null, actualStatus);
	}


	public HTTP200Exception(UriInfo request, Throwable cause, Status actualStatus)
	{
		this(request, null, cause, actualStatus);
	}


	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause)
	{
		this(request, formParams, cause, Status.INTERNAL_SERVER_ERROR);
	}


	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause, Status actualStatus)
	{
		super(cause);
		this.actualStatus = actualStatus;
		this.request = request;
		this.formParams = formParams;
	}


	public Map<String, Object> getInfo()
	{
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("requestUri", request.getRequestUri().toString());

		HashMap<String, Object> httpStatusInfo = new LinkedHashMap<>();
		info.put("httpStatus", httpStatusInfo);
		httpStatusInfo.put("code", actualStatus.getStatusCode());
		httpStatusInfo.put("message", actualStatus.toString());

		if (getCause() != null) {

			Throwable rootCause = getCause();
			while (rootCause.getCause() != null) {
				rootCause = rootCause.getCause();
			}

			HashMap<String, Object> exceptionInfo = new LinkedHashMap<>();
			info.put("exception", exceptionInfo);
			exceptionInfo.put("message", rootCause.getMessage());
			if (rootCause != getCause()) {
				exceptionInfo.put("cause", getCause().getClass().getName());
			}
			exceptionInfo.put("rootCause", rootCause.getClass().getName());
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
		
		QueryParams params = new QueryParams();
		params.addParams(request.getQueryParameters());
		if (formParams != null) {
			params.addParams(formParams);
		}
		info.put("queryParams", params);
		
		return info;
	}

}
