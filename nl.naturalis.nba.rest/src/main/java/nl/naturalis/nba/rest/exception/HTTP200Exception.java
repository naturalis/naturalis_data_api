package nl.naturalis.nba.rest.exception;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * A {@code HTTP200Exception} is thrown if the intent is to provide error
 * information within the response body. This can be useful for debugging
 * purposes. The exception is used within a workflow in which the HTTP request
 * seemingly completes normally (status 200 - OK), while the "actual status"
 * (e.g. 500 - INTERNAL SERVER ERROR) and other error information can be
 * retrieved from the {@code HTTP200Exception} instance and serialized to the
 * response body.
 * 
 * @author Ayco Holleman
 * 
 */
public class HTTP200Exception extends RESTException {

	public HTTP200Exception(UriInfo request, Status actualStatus)
	{
		super(request, actualStatus);
	}

	public HTTP200Exception(UriInfo request, Throwable cause, Status actualStatus)
	{
		super(request, cause, actualStatus);
	}

	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams,
			Throwable cause)
	{
		this(request, formParams, cause, INTERNAL_SERVER_ERROR);
	}

	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams,
			Throwable cause, Status actualStatus)
	{
		super(request, formParams, cause, actualStatus);
	}

}
