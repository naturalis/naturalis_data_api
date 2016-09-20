package nl.naturalis.nba.rest.exception;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

/**
 * @author Ayco Holleman
 *
 */
public class HTTP400Exception extends RESTException {

	public HTTP400Exception(UriInfo uriInfo, String message)
	{
		super(uriInfo, Status.BAD_REQUEST, "400 (BAD REQUEST)\n" + message);
	}

	public HTTP400Exception(UriInfo uriInfo, Throwable t)
	{
		super(uriInfo, t, Status.BAD_REQUEST);
	}

}
