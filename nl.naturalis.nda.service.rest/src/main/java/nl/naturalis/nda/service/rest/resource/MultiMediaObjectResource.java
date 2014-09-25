package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.search.SearchResultSet;

@Path("/multimedia")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class MultiMediaObjectResource {

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;
	
	


	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet list(@Context UriInfo request)
	{
		return new SearchResultSet<Specimen>();
	}
	

}
