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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.service.rest.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/specimen")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class SpecimenResource {

	private static final Logger logger = LoggerFactory.getLogger(SpecimenResource.class);

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;


	@GET
	@Path("/get-specimen")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> getSpecimenDetail(@Context UriInfo request)
	{
		logger.debug("getSpecimenDetail");
		SearchResultSet<Specimen> result = null;
		try {
			String unitID = request.getQueryParameters().getFirst("unitID");
			result = registry.getSpecimenDao().getSpecimenDetail(unitID);
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
		if (result == null) {
			throw ResourceUtil.handleError(request, Status.NOT_FOUND);
		}
		ResourceUtil.addDefaultRestLinks(result, request, false);
		return result;
	}


	@GET
	@Path("/get-specimen-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> getSpecimenDetailWithinResultSetGET(@Context UriInfo request)
	{
		try {
			logger.debug("getSpecimenDetailWithinResultSetGET");
			QueryParams params = new QueryParams(request.getQueryParameters());
			SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().getSpecimenDetailWithinSearchResult(params);
			ResourceUtil.addDefaultRestLinks(result, request, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@POST
	@Path("/get-specimen-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SearchResultSet<Specimen> getSpecimenDetailWithinResultSetPOST(MultivaluedMap<String, String> form, @Context UriInfo request)
	{
		try {
			logger.debug("getSpecimenDetailWithinResultSetPOST");
			QueryParams params = new QueryParams(form);
			SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().getSpecimenDetailWithinSearchResult(params);
			ResourceUtil.addDefaultRestLinks(result, request, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> searchGET(@Context UriInfo request)
	{
		try {
			logger.debug("searchGET");
			QueryParams params = new QueryParams(request.getQueryParameters());
			SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().specimenSearch(params);
			ResourceUtil.addDefaultRestLinks(result, request, true);
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
	public SearchResultSet<Specimen> searchPOST(MultivaluedMap<String, String> form, @Context UriInfo request)
	{
		try {
			logger.debug("searchPOST");
			QueryParams params = new QueryParams(form);
			SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().specimenSearch(params);
			ResourceUtil.addDefaultRestLinks(result, request, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@GET
	@Path("/name-search")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultGroupSet<Specimen, String> nameSearchGET(@Context UriInfo request)
	{
		try {
			logger.debug("nameSearchGET");
			QueryParams params = new QueryParams(request.getQueryParameters());
			ResultGroupSet<Specimen, String> result = registry.getBioportalSpecimenDao().specimenNameSearch(params);
			ResourceUtil.addDefaultRestLinks(result, request, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}


	@POST
	@Path("/name-search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ResultGroupSet<Specimen, String> nameSearchPOST(MultivaluedMap<String, String> form, @Context UriInfo request)
	{
		try {
			logger.debug("nameSearchPOST");
			QueryParams params = new QueryParams(form);
			ResultGroupSet<Specimen, String> result = registry.getBioportalSpecimenDao().specimenNameSearch(params);
			ResourceUtil.addDefaultRestLinks(result, request, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(request, t);
		}
	}

}
