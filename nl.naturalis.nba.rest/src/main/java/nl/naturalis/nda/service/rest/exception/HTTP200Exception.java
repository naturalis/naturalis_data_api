package nl.naturalis.nda.service.rest.exception;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * A {@code HTTP200Exception} is thrown if the intent is to provide error
 * information within the response body. This can be useful for debugging
 * purposes. The exception is used within a workflow in which the HTTP request
 * seemingly completes normally (status 200 - OK), while the "actual status"
 * (most likely 500 - INTERNAL SERVER ERROR) and other error information can be
 * retrieved from the {@code HTTP200Exception} object and serialized to the
 * response body.
 * 
 * @author Ayco Holleman
 * 
 */
@SuppressWarnings("serial")
public class HTTP200Exception extends RESTException {

	public HTTP200Exception(UriInfo request, Status actualStatus)
	{
		super(request, actualStatus);
	}


	public HTTP200Exception(UriInfo request, Throwable cause, Status actualStatus)
	{
		super(request, cause, actualStatus);
	}


	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause)
	{
		this(request, formParams, cause, Status.INTERNAL_SERVER_ERROR);
	}


	public HTTP200Exception(UriInfo request, MultivaluedMap<String, String> formParams, Throwable cause, Status actualStatus)
	{
		super(request, formParams, cause, actualStatus);
	}

}
