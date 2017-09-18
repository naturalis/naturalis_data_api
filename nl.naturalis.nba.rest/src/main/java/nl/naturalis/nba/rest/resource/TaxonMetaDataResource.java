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
import nl.naturalis.nba.dao.TaxonMetaDataDao;
import nl.naturalis.nba.utils.ConfigObject;

@Stateless
@LocalBean
@SuppressWarnings("static-method")
@Api(value = "taxon")
@Path("/taxon/metadata")

public class TaxonMetaDataResource extends NbaDocumentMetaDataResource<TaxonMetaDataDao> {

	TaxonMetaDataResource()
	{
		super(new TaxonMetaDataDao());
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(TaxonMetaDataResource.class);

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
		return super.getSettings(name, uriInfo);
	}

	@GET
	@Path("/getSettings")
	@ApiOperation(value = "List all publicly available configuration settings for the NBA", response = Map.class, notes = "The value of a specific setting can be queried with metadata/getSetting/{name}")
	@Produces(JSON_CONTENT_TYPE)
	public Map<NbaSetting, Object> getSettings(@Context UriInfo uriInfo)
	{
		try {
			return new TaxonMetaDataDao().getSettings();
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
			TaxonMetaDataDao dao = new TaxonMetaDataDao();
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
			TaxonMetaDataDao dao = new TaxonMetaDataDao();
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
			@ApiParam(value = "specimen document field", required = true, defaultValue = "taxonRank") @PathParam("field") String field,
			@ApiParam(value = "operator", required = true, defaultValue = "EQUALS") @PathParam("operator") String operator,
			@Context UriInfo uriInfo)
	{
		try {
			ComparisonOperator op = ComparisonOperator.parse(operator);
			TaxonMetaDataDao dao = new TaxonMetaDataDao();
			return dao.isOperatorAllowed(field, op);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
}
