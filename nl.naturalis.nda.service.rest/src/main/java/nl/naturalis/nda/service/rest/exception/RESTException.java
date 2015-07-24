package nl.naturalis.nda.service.rest.exception;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import nl.naturalis.nda.search.QueryParams;

/**
 * Base {@code Exception} thrown by resource methods. Resource methods must trap
 * any error condition within a try/catch-all block and wrap the
 * {@code Exception} in a {@code RESTException}. A {@code RESTApplication}
 * maintains information about the HTTP request that resulted in the error
 * condition.
 * 
 * @author Ayco Holleman
 *
 */
public class RESTException extends RuntimeException {

	private static final long serialVersionUID = 9030540045749394408L;

	private final Status status;
	private final UriInfo request;
	private final MultivaluedMap<String, String> formParams;


	public RESTException(UriInfo request, Status actualStatus)
	{
		this(request, null, null, actualStatus);
	}


	public RESTException(UriInfo request, Throwable cause, Status actualStatus)
	{
		this(request, null, cause, actualStatus);
	}


	public RESTException(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause)
	{
		this(request, formParams, cause, Status.INTERNAL_SERVER_ERROR);
	}


	public RESTException(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause, Status status)
	{
		super(cause);
		this.status = status;
		this.request = request;
		this.formParams = formParams;
	}


	public Status getStatus()
	{
		return status;
	}


	/**
	 * 
	 * <code>
	 * 	info: {
	 * 		requestUri: (string),
	 * 		httpStatus: {
	 * 			code: (int),
	 * 			message: (string)
	 * 		},
	 * 		exception: {
	 * 			message: (string),
	 * 			cause: (string),
	 * 			rootCause: (string),
	 * 			stackTrace: (string[])
	 * 		},
	 * 		params: (map)
	 * 	}
	 * </code>
	 * 
	 * @return
	 */
	public Map<String, Object> getInfo()
	{
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		info.put("requestUri", request.getRequestUri().toString());

		HashMap<String, Object> httpStatusInfo = new LinkedHashMap<>();
		info.put("httpStatus", httpStatusInfo);
		httpStatusInfo.put("code", status.getStatusCode());
		httpStatusInfo.put("message", status.toString());

		Throwable throwable = this;
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}

		HashMap<String, Object> exceptionInfo = new LinkedHashMap<>();
		info.put("exception", exceptionInfo);
		exceptionInfo.put("message", throwable.toString());
		StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		List<String> trace = new ArrayList<String>(stackTraceElements.length);
		for (StackTraceElement e : stackTraceElements) {
			StringBuilder sb = new StringBuilder(128);
			sb.append("at ");
			sb.append(e.getClassName()).append('.').append(e.getMethodName());
			sb.append('(').append(e.getFileName()).append(':').append(e.getLineNumber()).append(')');
			trace.add(sb.toString());
		}
		exceptionInfo.put("stackTrace", trace);

		QueryParams params = new QueryParams();
		params.addParams(request.getPathParameters());
		params.addParams(request.getQueryParameters());
		if (formParams != null) {
			params.addParams(formParams);
		}
		info.put("queryParams", params);

		return info;
	}

}
