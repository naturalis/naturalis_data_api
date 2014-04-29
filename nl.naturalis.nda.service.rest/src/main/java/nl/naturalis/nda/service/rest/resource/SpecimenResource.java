package nl.naturalis.nda.service.rest.resource;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import nl.naturalis.nda.domain.Specimen;

@Path("/specimen")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class SpecimenResource {

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Specimen getSpecimen(@PathParam("id") String id)
	{
		Specimen specimen = new Specimen();
		specimen.setBarcode(id);
		return specimen;
	}

}
