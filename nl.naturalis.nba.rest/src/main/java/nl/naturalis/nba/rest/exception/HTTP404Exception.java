package nl.naturalis.nba.rest.exception;

import javax.ws.rs.core.Response.Status;

import nl.naturalis.nba.dao.DocumentType;

import javax.ws.rs.core.UriInfo;

/**
 * Thrown when a resource (a Taxon document, Specimen document, etc&#46;) could
 * not be found for a client-provided ID.
 * 
 * @author Ayco Holleman
 *
 */
public class HTTP404Exception extends RESTException {

	private static String getMessage(DocumentType<?> type, String id)
	{
		String fmt = "404 (NOT FOUND)\nNo %s exists with ID %s";
		return String.format(fmt, type, id);
	}

	public HTTP404Exception(UriInfo uriInfo, DocumentType<?> type, String id)
	{
		super(uriInfo, Status.NOT_FOUND, getMessage(type, id));
	}

	public HTTP404Exception(UriInfo uriInfo, String message)
	{
		super(uriInfo, Status.NOT_FOUND, "404 (NOT FOUND)\n" + message);
	}

}
