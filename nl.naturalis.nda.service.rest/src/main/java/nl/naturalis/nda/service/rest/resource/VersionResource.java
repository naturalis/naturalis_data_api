package nl.naturalis.nda.service.rest.resource;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/version")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class VersionResource {

	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String show()
	{
		return "0.9.016";
	}

}
