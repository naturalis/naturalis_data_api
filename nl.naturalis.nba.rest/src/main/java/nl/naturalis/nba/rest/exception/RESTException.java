package nl.naturalis.nba.rest.exception;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.common.rest.ExceptionInfo;
import nl.naturalis.nba.common.rest.HttpStatusInfo;
import nl.naturalis.nba.common.rest.ServerInfo;

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

	private Status status;
	private UriInfo request;

	public RESTException(UriInfo request, Throwable cause)
	{
		super(cause);
		this.request = request;
		this.status = Status.INTERNAL_SERVER_ERROR;
	}

	public RESTException(UriInfo request, Status status, String message)
	{
		super(message);
		this.request = request;
		this.status = status;
	}

	public Status getStatus()
	{
		return status;
	}

	public ServerInfo getInfo()
	{
		Throwable throwable = this;
		while (throwable.getCause() != null) {
			throwable = throwable.getCause();
		}
		ServerInfo inf = new ServerInfo();
		inf.setRequestUri(request.getRequestUri().toString());
		HttpStatusInfo httpStatus = new HttpStatusInfo(status.getStatusCode(), status.toString());
		inf.setHttpStatus(httpStatus);
		ExceptionInfo exception = new ExceptionInfo();
		exception.setMessage(throwable.getMessage());
		exception.setType(throwable.getClass());
		exception.setRegularStackTrace(throwable.getStackTrace());
		inf.setException(exception);
		return inf;

		//		Map<String, Object> info = new LinkedHashMap<>();
		//		info.put("requestUri", request.getRequestUri().toString());
		//
		//		HashMap<String, Object> httpStatusInfo = new LinkedHashMap<>();
		//		info.put("httpStatus", httpStatusInfo);
		//		httpStatusInfo.put("code", status.getStatusCode());
		//		httpStatusInfo.put("message", status.toString());
		//
		//		HashMap<String, Object> exceptionInfo = new LinkedHashMap<>();
		//		info.put("exception", exceptionInfo);
		//		exceptionInfo.put("message", throwable.getMessage());
		//		exceptionInfo.put("cause", throwable.getClass().getName());
		//		StackTraceElement[] stackTrace = throwable.getStackTrace();
		//		List<String> trace = new ArrayList<>(stackTrace.length);
		//		for (StackTraceElement e : stackTrace) {
		//			StringBuilder sb = new StringBuilder(128);
		//			sb.append("at ");
		//			sb.append(e.getClassName()).append('.').append(e.getMethodName());
		//			sb.append('(').append(e.getFileName()).append(':').append(e.getLineNumber())
		//					.append(')');
		//			trace.add(sb.toString());
		//		}
		//		exceptionInfo.put("stackTrace", trace);
		//		return info;
	}

}
