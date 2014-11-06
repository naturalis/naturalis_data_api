package nl.naturalis.nda.service.rest.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.search.AbstractResultSet;
import nl.naturalis.nda.service.rest.exception.HTTP200Exception;

public class ResourceUtil {

	private static final String ERROR_AS_JSON = "_jsonError";


	public static RuntimeException handleError(UriInfo request, Throwable throwable, Status status)
	{
		boolean returnExceptionAsJson = false;
		String s = request.getQueryParameters().getFirst(ERROR_AS_JSON);
		if (s != null) {
			if (s.length() == 0 || s.equals("1")) {
				returnExceptionAsJson = true;
			}
			else if (s.toUpperCase().equals("TRUE")) {
				returnExceptionAsJson = true;
			}
		}
		if (returnExceptionAsJson) {
			return new HTTP200Exception(request, throwable, status);
		}
		return throwable instanceof RuntimeException ? ((RuntimeException) throwable) : new RuntimeException(throwable);
	}


	public static RuntimeException handleError(UriInfo request, Throwable throwable)
	{
		boolean returnExceptionAsJson = false;
		String s = request.getQueryParameters().getFirst(ERROR_AS_JSON);
		if (s != null) {
			if (s.length() == 0 || s.equals("1")) {
				returnExceptionAsJson = true;
			}
			else if (s.toUpperCase().equals("TRUE")) {
				returnExceptionAsJson = true;
			}
		}
		if (returnExceptionAsJson) {
			return new HTTP200Exception(request, throwable, Status.INTERNAL_SERVER_ERROR);
		}
		return throwable instanceof RuntimeException ? ((RuntimeException) throwable) : new RuntimeException(throwable);
	}


	public static RuntimeException handleError(UriInfo request, Status status)
	{
		boolean returnExceptionAsJson = false;
		String s = request.getQueryParameters().getFirst(ERROR_AS_JSON);
		if (s != null) {
			if (s.length() == 0 || s.equals("1")) {
				returnExceptionAsJson = true;
			}
			else if (s.toUpperCase().equals("TRUE")) {
				returnExceptionAsJson = true;
			}
		}
		if (returnExceptionAsJson) {
			return new HTTP200Exception(request, status);
		}
		return new WebApplicationException(status);
	}


	public static void addDefaultRestLinks(AbstractResultSet result, UriInfo request, boolean forcePrevNextLinks)
	{
		result.addLink("_self", request.getRequestUri().toString());
		String offset = request.getQueryParameters().getFirst("_offset");
		String maxResults = request.getQueryParameters().getFirst("_maxResults");
		if (offset == null || offset.trim().length() == 0) {
			offset = "0";
		}
		if (maxResults == null || maxResults.trim().length() == 0) {
			maxResults = "10";
		}
		int iOffset = Integer.parseInt(offset);
		int iMaxResults = Integer.parseInt(maxResults);
		iOffset += iMaxResults;

	}

}
