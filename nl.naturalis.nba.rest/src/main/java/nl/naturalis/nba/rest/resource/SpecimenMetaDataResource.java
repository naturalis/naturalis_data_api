package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.dao.SpecimenMetaDataDao;

@SuppressWarnings("static-method")
@Path("/specimen/metadata")
@Stateless
@LocalBean
public class SpecimenMetaDataResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenMetaDataResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/getMapping")
	@Produces(JSON_CONTENT_TYPE)
	public String getMapping(@Context UriInfo uriInfo)
	{
		try {
			SpecimenMetaDataDao dao = new SpecimenMetaDataDao();
			return dao.getMapping();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
