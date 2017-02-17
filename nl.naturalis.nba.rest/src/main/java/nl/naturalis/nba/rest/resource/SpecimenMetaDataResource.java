package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.dao.SpecimenMetaDataDao;
import nl.naturalis.nba.utils.ConfigObject;

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

	@GET
	@Path("/getPaths")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getPaths(@Context UriInfo uriInfo)
	{
		try {
			SpecimenMetaDataDao dao = new SpecimenMetaDataDao();
			String s = uriInfo.getQueryParameters().getFirst("sorted");
			boolean sorted = ConfigObject.isTrueValue(s);
			return dao.getPaths(sorted);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getAllowedOperators")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, Set<ComparisonOperator>> getAllowedOperators(@Context UriInfo uriInfo)
	{
		try {
			SpecimenMetaDataDao dao = new SpecimenMetaDataDao();
			String param = uriInfo.getQueryParameters().getFirst("fields");
			String[] fields = null;
			if (param != null) {
				fields = param.split(",");
			}
			return dao.getAllowedOperators(fields);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/isOperatorAllowed/{field}/{operator}")
	@Produces(JSON_CONTENT_TYPE)
	public boolean isOperatorAllowed(@PathParam("field") String field,
			@PathParam("operator") String operator, @Context UriInfo uriInfo)
	{
		try {
			ComparisonOperator op = ComparisonOperator.parse(operator);
			SpecimenMetaDataDao dao = new SpecimenMetaDataDao();
			return dao.isOperatorAllowed(field, op);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
