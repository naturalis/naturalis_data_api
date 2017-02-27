package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;

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
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.dao.MultiMediaObjectMetaDataDao;
import nl.naturalis.nba.utils.ConfigObject;

@SuppressWarnings("static-method")
@Path("/multimedia/metadata")
@Stateless
@LocalBean
public class MultiMediaObjectMetaDataResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager
			.getLogger(MultiMediaObjectMetaDataResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/getPaths")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getPaths(@Context UriInfo uriInfo)
	{
		try {
			MultiMediaObjectMetaDataDao dao = new MultiMediaObjectMetaDataDao();
			String s = uriInfo.getQueryParameters().getFirst("sorted");
			boolean sorted = ConfigObject.isTrueValue(s);
			return dao.getPaths(sorted);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getFieldInfo")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, FieldInfo> getFieldInfo(@Context UriInfo uriInfo)
	{
		try {
			MultiMediaObjectMetaDataDao dao = new MultiMediaObjectMetaDataDao();
			String param = uriInfo.getQueryParameters().getFirst("fields");
			String[] fields = null;
			if (param != null) {
				fields = param.split(",");
			}
			return dao.getFieldInfo(fields);
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
			MultiMediaObjectMetaDataDao dao = new MultiMediaObjectMetaDataDao();
			return dao.isOperatorAllowed(field, op);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
