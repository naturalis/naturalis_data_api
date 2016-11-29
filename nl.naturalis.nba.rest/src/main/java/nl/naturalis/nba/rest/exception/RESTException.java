package nl.naturalis.nba.rest.exception;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * Base {@code Exception} thrown by resource methods. Resource methods (i.e.
 * methods with an {@link Path &#64;Path} annotation) <b>must</b> trap any error
 * condition within a try/catch-all block and wrap the {@code Exception} in a
 * {@code RESTException}. A {@code RESTApplication} maintains information about
 * the HTTP request that resulted in the error condition.
 * 
 * @author Ayco Holleman
 *
 */
public class RESTException extends RuntimeException {

	private final Status status;
	private final UriInfo request;
	@SuppressWarnings("unused")
	private final MultivaluedMap<String, String> formParams;

	public RESTException(UriInfo request, Status status)
	{
		this(request, null, null, status);
	}

	public RESTException(UriInfo request, Status status, String message)
	{
		super(message);
		this.request = request;
		this.status = status;
		this.formParams = null;
	}

	public RESTException(UriInfo request, Throwable cause)
	{
		this(request, null, cause, INTERNAL_SERVER_ERROR);
	}

	public RESTException(UriInfo request, Throwable cause, Status status)
	{
		this(request, null, cause, status);
	}

	public RESTException(UriInfo request, MultivaluedMap<String, String> formParams,
			Throwable cause)
	{
		this(request, formParams, cause, INTERNAL_SERVER_ERROR);
	}

	public RESTException(UriInfo request, MultivaluedMap<String, String> formParams,
			Throwable cause, Status status)
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
	 * 	info: {<br>
	 * 		requestUri: (string),<br>
	 * 		httpStatus: {<br>
	 * 			code: (int),<br>
	 * 			message: (string)<br>
	 * 		},<br>
	 * 		exception: {<br>
	 * 			message: (string),<br>
	 * 			cause: (string),<br>
	 * 			rootCause: (string),<br>
	 * 			stackTrace: (string[])<br>
	 * 		},<br>
	 * 		params: (map)<br>
	 * 	}<br>
	 * </code>
	 * 
	 * @return
	 */
	public Map<String, Object> getInfo()
	{
		Map<String, Object> info = new LinkedHashMap<>();
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
		List<String> trace = new ArrayList<>(stackTraceElements.length);
		for (StackTraceElement e : stackTraceElements) {
			StringBuilder sb = new StringBuilder(128);
			sb.append("at ");
			sb.append(e.getClassName()).append('.').append(e.getMethodName());
			sb.append('(').append(e.getFileName()).append(':').append(e.getLineNumber())
					.append(')');
			trace.add(sb.toString());
		}
		exceptionInfo.put("stackTrace", trace);

		//		QueryParams params = new QueryParams();
		//		params.addParams(request.getPathParameters());
		//		params.addParams(request.getQueryParameters());
		//		if (formParams != null) {
		//			params.addParams(formParams);
		//		}
		//		info.put("queryParams", params);

		return info;
	}

}
