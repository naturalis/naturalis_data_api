package nl.naturalis.nba.rest.exception;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.api.model.ObjectType;

/**
 * @author Ayco Holleman
 * @created Jul 27, 2015
 *
 */
public class HTTP404Exception extends RESTException {

	private static final long serialVersionUID = -1054247707052058145L;


	private static String getMessage(ObjectType objectType, String objectID)
	{
		String fmt = "404 (NOT FOUND)\nNo %s exists with ID %s";
		return String.format(fmt, objectType, objectID);
	}


	public HTTP404Exception(UriInfo uriInfo, ObjectType objectType, String objectID)
	{
		super(uriInfo, Status.NOT_FOUND, getMessage(objectType, objectID));
	}

}
