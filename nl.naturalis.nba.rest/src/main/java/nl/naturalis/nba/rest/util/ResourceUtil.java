package nl.naturalis.nba.rest.util;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.rest.exception.RESTException;

public class ResourceUtil {

	/**
	 * The JSON media type including the charset specification:
	 * "application/json;charset=UTF-8". Some (older) browsers and REST clients
	 * seem to require the explicit charset specification in order to process
	 * the JSON response properly. Therefore resource methods should not
	 * {@link Produces produce} {@link MediaType#APPLICATION_JSON}, since that
	 * constant does not include the charset specification.
	 */
	public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

	public static RESTException handleError(UriInfo request, Throwable throwable)
	{
		if (throwable instanceof RESTException) {
			return (RESTException) throwable;
		}
		return new RESTException(request, throwable);
	}

}
