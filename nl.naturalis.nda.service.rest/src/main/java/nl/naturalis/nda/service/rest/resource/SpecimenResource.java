package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import nl.naturalis.nda.ejb.service.SpecimenService;

@Path("/specimen")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class SpecimenResource {

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;


//	@GET
//	@Path("/search")
//	@Produces(MediaType.APPLICATION_JSON)
//	public SearchResultSet search(@Context UriInfo request)
//	{
//		SpecimenDao dao = new SpecimenDao(registry.getESClient(), "nda");
//		QueryParams params = new QueryParams(request.getQueryParameters());
//		OccurrenceSearchResultSet result = dao.listSpecimens(params);
//		return result;
//	}
//
//
//	@GET
//	@Path("/{id}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Occurrence getSpecimen(@PathParam("id") String id)
//	{
//		Occurrence specimen = new Occurrence();
//		return specimen;
//	}

}
