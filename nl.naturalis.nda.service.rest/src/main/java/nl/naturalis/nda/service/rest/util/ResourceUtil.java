package nl.naturalis.nda.service.rest.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.search.AbstractResultSet;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.service.rest.exception.HTTP200Exception;

public class ResourceUtil {
	
	public static final String JSON_CONTENT_TYPE = "application/json;charset=UTF-8";

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
		return handleError(request, null, throwable);
	}


	public static RuntimeException handleError(UriInfo request, MultivaluedMap<String, String> form, Throwable throwable)
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
			return new HTTP200Exception(request, form, throwable);
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


	public static void doAfterDao(AbstractResultSet result, UriInfo request, boolean addNavigationLinks)
	{
		doAfterDao(result, request, null, addNavigationLinks);
	}


	public static void doAfterDao(AbstractResultSet result, UriInfo request, MultivaluedMap<String, String> formParams, boolean addNavigationLinks)
	{
		QueryParams params = new QueryParams();
		params.addParams(request.getQueryParameters());
		if (formParams != null) {
			params.addParams(formParams);
		}
		result.setQueryParameters(params);
		result.addLink("_self", request.getRequestUri().toString());
		if (addNavigationLinks) {
			String offset = request.getQueryParameters().getFirst("_offset");
			boolean hasOffset = true;
			String maxResults = request.getQueryParameters().getFirst("_maxResults");
			if (offset == null || offset.trim().length() == 0) {
				hasOffset = false;
				offset = "0";
			}
			if (maxResults == null || maxResults.trim().length() == 0) {
				maxResults = "10";
			}
			long iOffset = Long.parseLong(offset);
			long iMaxResults = Long.parseLong(maxResults);
			long offsetNext = iOffset + iMaxResults;
			long offsetPrev = Math.max(0, iOffset - iMaxResults);
			UriBuilder prevLinkUriBuilder = request.getRequestUriBuilder();
			UriBuilder nextLinkUriBuilder = request.getRequestUriBuilder();

			if (offsetNext < result.getTotalSize()) {
				if (hasOffset) {
					nextLinkUriBuilder.replaceQueryParam("_offset", String.valueOf(offsetNext));
				}
				else {
					nextLinkUriBuilder.queryParam("_offset", String.valueOf(offsetNext));
				}
			}

			if (offsetPrev == 0) {
				if (hasOffset) {
					prevLinkUriBuilder.replaceQueryParam("_offset", "0");
				}
			}
			else {
				if (hasOffset) {
					prevLinkUriBuilder.replaceQueryParam("_offset", String.valueOf(offsetPrev));
				}
				else {
					prevLinkUriBuilder.queryParam("_offset", String.valueOf(offsetPrev));
				}
			}

			result.addLink("_prevPage", prevLinkUriBuilder.build().toString());
			result.addLink("_nextPage", nextLinkUriBuilder.build().toString());
		}

	}

}
