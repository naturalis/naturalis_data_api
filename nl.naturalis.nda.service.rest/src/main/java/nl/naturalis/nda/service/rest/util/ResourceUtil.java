package nl.naturalis.nda.service.rest.util;

import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.search.AbstractResultSet;

public class ResourceUtil {

	public static void addDefaultRestLinks(AbstractResultSet result, UriInfo request, boolean forcePrevNextLinks)
	{
		result.addLink("_self", request.getRequestUri().toString());
		String offset = request.getQueryParameters().getFirst("_offset");
		String maxResults = request.getQueryParameters().getFirst("_maxResults");
		if(offset == null || offset.trim().length()==0) {
			offset = "0";
		}
		if(maxResults == null || maxResults.trim().length() == 0) {
			maxResults = "10";
		}
		int iOffset = Integer.parseInt(offset);
		int iMaxResults = Integer.parseInt(maxResults);
		iOffset += iMaxResults;
		
	}

}
