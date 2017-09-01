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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.dao.GeoAreaMetaDataDao;
import nl.naturalis.nba.utils.ConfigObject;

@SuppressWarnings("static-method")
@Path("/geo/metadata")
@Stateless
@LocalBean
@Api(value = "geo")

public class GeoAreaMetaDataResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(GeoAreaMetaDataResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/getSetting/{name}")
	@ApiOperation(value = "Get the value of an NBA setting", response = Object.class, notes = "All settings can be queried with /metadata/getSettings")
	@Produces(JSON_CONTENT_TYPE)
	public Object getSettings(
			@ApiParam(value = "name of setting", required = true, defaultValue = "index.max_result_window") @PathParam("name") String name,
			@Context UriInfo uriInfo)
	{
		try {
			NbaSetting setting = NbaSetting.parse(name);
			return new GeoAreaMetaDataDao().getSetting(setting);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getSettings")
	@ApiOperation(value = "List all publicly available configuration settings for the NBA", response = Map.class, notes = "The value of a specific setting can be queried with metadata/getSetting/{name}")
	@Produces(JSON_CONTENT_TYPE)
	public Map<NbaSetting, Object> getSettings(@Context UriInfo uriInfo)
	{
		try {
			return new GeoAreaMetaDataDao().getSettings();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getPaths")
	@ApiOperation(value = "Returns the full path of all fields within a document", response = String[].class, notes = "See also metadata/getFieldInfo for all allowed operators per field")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getPaths(@Context UriInfo uriInfo)
	{
		try {
			GeoAreaMetaDataDao dao = new GeoAreaMetaDataDao();
			String s = uriInfo.getQueryParameters().getFirst("sorted");
			boolean sorted = ConfigObject.isTrueValue(s);
			return dao.getPaths(sorted);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getFieldInfo")
	@ApiOperation(value = "Returns extended information for each field of a specimen document", response = Map.class, notes = "Info consists of whether the fields is indexed, the ElasticSearch datatype and a list of allowed operators")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, FieldInfo> getFieldInfo(@Context UriInfo uriInfo)
	{
		try {
			GeoAreaMetaDataDao dao = new GeoAreaMetaDataDao();
			String param = uriInfo.getQueryParameters().getFirst("fields");
			String[] fields = null;
			if (param != null) {
				fields = param.split(",");
			}
			return dao.getFieldInfo(fields);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/isOperatorAllowed/{field}/{operator}")
	@ApiOperation(value = "Checks if a given operator is allowed for a given field", response = Map.class, notes = "See also metadata/getFieldInfo")
	@Produces(JSON_CONTENT_TYPE)
	public boolean isOperatorAllowed(
			@ApiParam(value = "Field in geo area document", required = true, defaultValue = "locality") @PathParam("field") String field,
			@ApiParam(value = "operator", required = true, defaultValue = "EQUALS") @PathParam("operator") String operator,
			@Context UriInfo uriInfo)
	{
		try {
			ComparisonOperator op = ComparisonOperator.parse(operator);
			GeoAreaMetaDataDao dao = new GeoAreaMetaDataDao();
			return dao.isOperatorAllowed(field, op);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
}
