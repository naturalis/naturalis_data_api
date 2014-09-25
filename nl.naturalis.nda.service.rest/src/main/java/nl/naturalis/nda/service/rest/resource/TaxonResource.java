package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.elasticsearch.dao.dao.TaxonDao;
import nl.naturalis.nda.search.SearchResultSet;

@Path("/taxon")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class TaxonResource {

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> list(@Context UriInfo request)
	{
		return new SearchResultSet<Taxon>();
	}


	@GET
	@Path("/scientific-name/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> findByScientificName(@PathParam("name") String name)
	{
		TaxonDao dao = new TaxonDao(registry.getESClient(), "nda");
		SearchResultSet<Taxon> result = dao.findByScientificName(name);
		return result;
	}

}
