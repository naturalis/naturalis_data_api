package nl.naturalis.nda.service.rest.resource;

import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.elasticsearch.dao.dao.SpecimenDao;
import nl.naturalis.nda.search.SearchResultSet;

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
	@Path("/detail/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Specimen> getSpecimenDetail(@PathParam("id") String unitID)
	{
		logger.debug(String.format("getSpecimenDetail(\"%s\")", unitID));
		SpecimenDao dao = new SpecimenDao(registry.getESClient(), "nda");
		SearchResultSet<Specimen> rs = dao.getDetail(unitID);
		if(rs == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return rs;
	}

}
