package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResultSet;

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
	@POST
	@Path("/get-specimen")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> getSpecimenDetail(@Context UriInfo request)
	{
		logger.debug("getSpecimenDetail");
		String unitID = request.getQueryParameters().getFirst("unitID");
		SearchResultSet<Specimen> result = registry.getSpecimenDao().getSpecimenDetail(unitID);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@POST
	@Path("/get-specimen-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> getSpecimenDetailWithinResultSet(@Context UriInfo request)
	{
		logger.debug("getSpecimenDetailWithinResultSet");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().getSpecimenDetailWithinSearchResult(params);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> search(@Context UriInfo request)
	{
		logger.debug("search");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().specimenSearch(params);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@POST
	@Path("/extended-search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> extendedSearch(@Context UriInfo request)
	{
		logger.debug("extendedSearch");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Specimen> result = registry.getBioportalSpecimenDao().specimenSearch(params);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@POST
	@Path("/name-search")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultGroupSet<Specimen, String> nameSearch(@Context UriInfo request)
	{
		logger.debug("nameSearch");
		QueryParams params = new QueryParams(request.getQueryParameters());
		ResultGroupSet<Specimen, String> result = registry.getBioportalSpecimenDao().specimenNameSearch(params);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}


	@GET
	@POST
	@Path("/extended-name-search")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultGroupSet<Specimen, String> extendedNameSearch(@Context UriInfo request)
	{
		logger.debug("extendedNameSearch");
		QueryParams params = new QueryParams(request.getQueryParameters());
		ResultGroupSet<Specimen, String> result = registry.getBioportalSpecimenDao().specimenNameSearch(params);
		result.addLink("_self", request.getRequestUri().toString());
		return result;
	}

}
