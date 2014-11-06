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
import javax.ws.rs.core.Response.Status;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.service.rest.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/taxon")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class TaxonResource {

	private static final Logger logger = LoggerFactory.getLogger(TaxonResource.class);

	@EJB
	Registry registry;


	@GET
	@Path("/get-taxon")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetail(@Context UriInfo request)
	{
		logger.debug("getTaxonDetail");
		SearchResultSet<Taxon> result = null;
		try {
			QueryParams params = new QueryParams(request.getQueryParameters());
			result = registry.getTaxonDao().getTaxonDetail(params);
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
		if (result == null) {
			throw ResourceUtil.handleError(request, Status.NOT_FOUND);
		}
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@Path("/get-taxon-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetailWithinResultSet(@Context UriInfo request)
	{
		try {
			logger.debug("getTaxonDetailWithinResultSet");
			QueryParams params = new QueryParams(request.getQueryParameters());
			SearchResultSet<Taxon> result = registry.getBioportalTaxonDao().getTaxonDetailWithinResultSet(params);
			result.addLink("_self", request.getRequestUri().toString());
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultGroupSet<Taxon, String> searchGET(@Context UriInfo request)
	{
		try {
			logger.debug("searchGET");
			QueryParams params = new QueryParams(request.getQueryParameters());
			ResultGroupSet<Taxon, String> result = registry.getBioportalTaxonDao().taxonSearch(params);
			result.addLink("_self", request.getRequestUri().toString());
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
	public ResultGroupSet<Taxon, String> searchPOST(@Context UriInfo request, MultivaluedMap<String, String> form)
	{
		try {
			logger.debug("searchPOST");
			QueryParams params = new QueryParams(form);
			ResultGroupSet<Taxon, String> result = registry.getBioportalTaxonDao().taxonSearch(params);
			result.addLink("_self", request.getRequestUri().toString());
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}

}
