package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.dao.BioportalMultiMediaObjectDao;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.service.rest.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/multimedia")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class MultiMediaObjectResource {

	private static final Logger logger = LoggerFactory.getLogger(MultiMediaObjectResource.class);

	@EJB
	Registry registry;


	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> searchGET(@Context UriInfo request)
	{
		try {
			logger.debug("searchGET");
			QueryParams params = new QueryParams(request.getQueryParameters());
			SearchResultSet<MultiMediaObject> result = registry.getBioportalMultiMediaObjectDao().multiMediaObjectSearch(params);
			ResourceUtil.doAfterDao(result, request, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SearchResultSet<MultiMediaObject> searchPOST(@Context UriInfo request, MultivaluedMap<String, String> form)
	{
		try {
			logger.debug("searchPOST");
			QueryParams params = new QueryParams(form);
			SearchResultSet<MultiMediaObject> result = registry.getBioportalMultiMediaObjectDao().multiMediaObjectSearch(params);
			ResourceUtil.doAfterDao(result, request, form, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@GET
	@Path("/get-multimedia-object-for-taxon-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> getTaxonMultiMediaObjectDetailWithinResultSet(@Context UriInfo request)
	{
		try {
			logger.debug("getTaxonMultiMediaObjectDetailWithinResultSet");
			QueryParams params = new QueryParams(request.getQueryParameters());
			BioportalMultiMediaObjectDao dao = registry.getBioportalMultiMediaObjectDao();
			SearchResultSet<MultiMediaObject> result = dao.getTaxonMultiMediaObjectDetailWithinResultSet(params);
			ResourceUtil.doAfterDao(result, request, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@GET
	@Path("/get-multimedia-object-for-specimen-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> getSpecimenMultiMediaObjectDetailWithinResultSet(@Context UriInfo request)
	{
		try {
			logger.debug("getSpecimenMultiMediaObjectDetailWithinResultSet");
			QueryParams params = new QueryParams(request.getQueryParameters());
			BioportalMultiMediaObjectDao dao = registry.getBioportalMultiMediaObjectDao();
			SearchResultSet<MultiMediaObject> result = dao.getSpecimenMultiMediaObjectDetailWithinResultSet(params);
			ResourceUtil.doAfterDao(result, request, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}

}
