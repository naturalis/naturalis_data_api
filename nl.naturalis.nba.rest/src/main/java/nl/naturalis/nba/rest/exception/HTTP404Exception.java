package nl.naturalis.nba.rest.exception;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.dao.es.util.DocumentType;

/**
 * @author Ayco Holleman
 *
 */
public class HTTP404Exception extends RESTException {

	private static String getMessage(DocumentType type, String id)
	{
		String fmt = "404 (NOT FOUND)\nNo %s exists with ID %s";
		return String.format(fmt, type, id);
	}

	public HTTP404Exception(UriInfo uriInfo, DocumentType type, String id)
	{
		super(uriInfo, Status.NOT_FOUND, getMessage(type, id));
	}

}
