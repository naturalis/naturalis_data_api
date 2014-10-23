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

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.elasticsearch.dao.dao.BioportalMultiMediaObjectDao;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.SearchResultSet;

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
	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> search(@Context UriInfo request)
	{
		logger.debug("search");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SearchResultSet<MultiMediaObject> result = registry.getBioportalMultiMediaObjectDao().multiMediaObjectSearch(params);
		return result;
	}

	@GET
	@POST
	@Path("/get-multimedia-object-for-taxon-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> getTaxonMultiMediaObjectDetailWithinResultSet(@Context UriInfo request)
	{
		logger.debug("search");
		QueryParams params = new QueryParams(request.getQueryParameters());
		BioportalMultiMediaObjectDao dao = registry.getBioportalMultiMediaObjectDao();
		SearchResultSet<MultiMediaObject> result = dao.getTaxonMultiMediaObjectDetailWithinResultSet(params);
		return result;
	}

	@GET
	@POST
	@Path("/get-multimedia-object-for-specimen-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<MultiMediaObject> getSpecimenMultiMediaObjectDetailWithinResultSet(@Context UriInfo request)
	{
		logger.debug("getSpecimenMultiMediaObjectDetailWithinResultSet");
		QueryParams params = new QueryParams(request.getQueryParameters());
		BioportalMultiMediaObjectDao dao = registry.getBioportalMultiMediaObjectDao();
		SearchResultSet<MultiMediaObject> result = dao.getSpecimenMultiMediaObjectDetailWithinResultSet(params);
		return result;
	}

}
