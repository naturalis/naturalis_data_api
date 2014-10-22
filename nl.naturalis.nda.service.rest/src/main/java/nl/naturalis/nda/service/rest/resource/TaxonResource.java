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

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;

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
	@POST
	@Path("/detail")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetail(@Context UriInfo request)
	{
		logger.debug("getTaxonDetail");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Taxon> result = registry.getTaxonDao().getTaxonDetail(params);
		return result;
	}


	@GET
	@POST
	@Path("/within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetailWithinResultSet(@Context UriInfo request)
	{
		logger.debug("getTaxonDetailWithinResultSet");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Taxon> result = registry.getBioportalTaxonDao().getTaxonDetailWithinResultSet(params);
		return result;
	}


	@GET
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> search(@Context UriInfo request)
	{
		logger.debug("getTaxonDetailWithinResultSet");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Taxon> result = registry.getBioportalTaxonDao().taxonSearch(params);
		return result;
	}


	@GET
	@POST
	@Path("/extended-search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> extendedSearch(@Context UriInfo request)
	{
		logger.debug("getTaxonDetailWithinResultSet");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<Taxon> result = registry.getBioportalTaxonDao().taxonSearch(params);
		return result;
	}

}
