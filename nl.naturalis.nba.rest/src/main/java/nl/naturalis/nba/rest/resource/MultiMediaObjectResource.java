package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.dao.MultiMediaObjectDao;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;

@SuppressWarnings("static-method")
@Path("/multimedia")
@Stateless
@LocalBean
@Api(value = "multimedia")
public class MultiMediaObjectResource extends NbaResource<MultiMediaObject, MultiMediaObjectDao> {

	MultiMediaObjectResource()
	{
		super(new MultiMediaObjectDao());
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(MultiMediaObjectResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@ApiOperation(value = "Find a multimedia document by id", response = MultiMediaObject.class, notes = "If found, returns a single multimedia document")
	@Produces(JSON_CONTENT_TYPE)
	public MultiMediaObject find(
			@ApiParam(value = "id of multimedia document", required = true, defaultValue = "L.4169766_1307658521@BRAHMS") 
			@PathParam("id") String id, 
			@Context UriInfo uriInfo)
	{
		return super.find(id, uriInfo);
	}

	@GET
	@Path("/findByIds/{ids}")
	@ApiOperation(value = "Find multimedia document by ids", response = MultiMediaObject[].class, notes = "Given multiple ids, returns a list of multimedia documents")
	@Produces(JSON_CONTENT_TYPE)
	public MultiMediaObject[] findByIds(@ApiParam(value = "ids of multiple multimedia documents, separated by comma", required = true, defaultValue = "U.1475914_2059926060@BRAHMS,L.2454256_0837498402@BRAHMS", allowMultiple = true) 
	@PathParam("ids") String ids, 
	@Context UriInfo uriInfo)
	{
		return super.findByIds(ids, uriInfo);
	}

	@GET
	@Path("/query")
	@ApiOperation(value = "Query for multimedia documents", response = QueryResult.class, notes = "Search for multimedia documents with a human-readable query")
	@Produces(JSON_CONTENT_TYPE)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "license", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "CC0", required = false) })	
	public QueryResult<MultiMediaObject> query_GET(@Context UriInfo uriInfo)
	{
		return super.query_GET(uriInfo);
	}

	@POST
	@Path("/query")
	@ApiOperation(hidden = true, value = "Query for multimedia documents", response = QueryResult.class, notes = "Search for multimedia documents (POST)")	
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public QueryResult<MultiMediaObject> query_POST_FORM(@ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/query")
	@ApiOperation(value = "Query for taxa", response = QueryResult.class, notes = "Search for multimedia documents with a querySpec JSON")	
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public QueryResult<MultiMediaObject> query_POST_JSON(@ApiParam(value = "querySpec", required = false) QuerySpec qs, @Context UriInfo uriInfo)
	{
		try {
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/count")
	@ApiOperation(hidden = true, value = "Get the number of multimedia documents matching a condition", response = long.class, notes = "Conditions given in POST body")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public long count_POST_FORM(
			@ApiParam(value = "POST payload", required = false) 
			MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		return super.count_POST_FORM(form, uriInfo);
	}
	
	@POST
	@Path("/count")
	@ApiOperation(value = "Get the number of multimedia documents matching a condition", response = long.class, notes = "Conditions given as querySpec JSON")	
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public long count_POST_JSON(
			@ApiParam(value = "querySpec JSON", required = false) 
			QuerySpec qs, 
			@Context UriInfo uriInfo)
	{
		return super.count_POST_JSON(qs, uriInfo);
	}
	
	@GET
	@Path("/count")
	@ApiOperation(value = "Get the number of multimedia documents matching a condition", response = long.class, notes = "Conditions given as query string")	
	@Produces(JSON_CONTENT_TYPE)
	@ApiImplicitParams({
		@ApiImplicitParam(name = "sourceSystem.code", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "BRAHMS", required = false) })
	public long count_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.count(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getDistinctValues/{field}")
	@ApiOperation(value = "Get all different values that can be found for one field", response = Map.class, notes = "A list of all fields for multimedia documents can be retrieved with /metadata/getFieldInfo")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, Long> getDistinctValues(@ApiParam(value = "field", required = true, defaultValue = "gatheringEvents.worldRegion") @PathParam("field") String field,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.getDistinctValues(field, qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
