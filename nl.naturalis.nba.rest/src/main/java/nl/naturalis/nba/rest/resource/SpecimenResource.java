package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.TEXT_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.ZIP_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.exception.RESTException;
import nl.naturalis.nba.rest.util.HttpGroupByScientificNameQuerySpecBuilder;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

@Stateless
@LocalBean
@Api(value = "specimen")
@Path("/specimen")
@SuppressWarnings("static-method")
public class SpecimenResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenResource.class);
	@SuppressWarnings("unused")
	private static final int Specimen = 0;

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@ApiOperation(value = "Find a specimen by id", response = Specimen.class, notes = "If found, returns a single specimen")
	@ApiResponses(value = { @ApiResponse(code = 404, message = "id not found") })
	@Produces(JSON_CONTENT_TYPE)
	public Specimen find(
			@ApiParam(value = "id of specimen", required = true, defaultValue = "RMNH.MAM.17209.B@CRS") @PathParam("id") String id,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			Specimen result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.SPECIMEN, id);
			}
			return result;
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/findByIds/{ids}")
	@ApiOperation(value = "Find specimens by ids", response = Specimen[].class, notes = "Given multiple ids, returns a list of specimen")
	@Produces(JSON_CONTENT_TYPE)
	public Specimen[] findByIds(
			@ApiParam(value = "ids of multiple specimen, separated by comma", required = true, defaultValue = "RMNH.MOL.326483@CRS,ZMA.MAM.4211@CRS", allowMultiple = true) @PathParam("ids") String ids,
			@Context UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			SpecimenDao dao = new SpecimenDao();
			return dao.findByIds(idArray);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/findByUnitID/{unitID}")
	@ApiOperation(value = "Find a specimen by unitID", response = Specimen[].class, notes = "Get a specimen by its unitID. Returns a list of specimens since unitIDs are not strictly unique")
	@Produces(JSON_CONTENT_TYPE)
	public Specimen[] findByUnitID(
			@ApiParam(value = "the unitID of the specimen to query", required = true, defaultValue = "RMNH.MAM.17209.B") @PathParam("unitID") String unitID,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.findByUnitID(unitID);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/exists/{unitID}")
	@ApiOperation(value = "Returns whether or not a unitID for a specimen exists", response = boolean.class, notes = "Returns either true or false")
	@Produces(TEXT_CONTENT_TYPE)
	public boolean exists(
			@ApiParam(value = "the unitID of the specimen to query", required = true, defaultValue = "RMNH.MAM.17209.B") @PathParam("unitID") String unitID,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.exists(unitID);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/query")
	@ApiOperation(value = "Query for specimens", response = QueryResult.class, notes = "Search for specimen with a human-readable query")
	@Produces(JSON_CONTENT_TYPE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "collectionType", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "Crustacea", required = false) })
	public QueryResult<Specimen> query_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/query")
	@ApiOperation(hidden = true, value = "Query for specimens", response = QueryResult.class, notes = "Search for specimen (POST)")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public QueryResult<Specimen> query_POST_FORM(
			@ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/query")
	@ApiOperation(value = "Query for specimens", response = QueryResult.class, notes = "Search for specimen with a querySpec JSON")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public QueryResult<Specimen> query_POST_JSON(@ApiParam(value = "querySpec", required = false) QuerySpec qs,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/count")
	@ApiOperation(hidden = true, value = "Get the number of specimens matching a condition", response = long.class, notes = "Conditions given in POST body")
	@Produces(TEXT_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public long count_POST_FORM(
			@ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.count(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/count")
	@ApiOperation(value = "Get the number of specimens matching a condition", response = long.class, notes = "Conditions given as querySpec JSON")
	@Produces(TEXT_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public long count_POST_JSON(@ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.count(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/count")
	@ApiOperation(value = "Get the number of specimens matching a condition", response = long.class, notes = "Conditions given as query string")
	@Produces(TEXT_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "collectionType", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "Crustacea", required = false) })
	public long count_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.count(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getDistinctValues/{field}")
	@ApiOperation(value = "Get all different values that exist for a field", response = Map.class, notes = "A list of all fields for specimen documents can be retrieved with /metadata/getFieldInfo")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, Long> getDistinctValues(
			@ApiParam(value = "name of field in specimen object", required = true, defaultValue = "identifications.defaultClassification.family") @PathParam("field") String field,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.getDistinctValues(field, qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/query")
	@ApiOperation(value = "Dynamic download service: Query for specimens and return result as Darwin Core Archive File", response = Response.class, notes = "Query can be human-readable or querySpec JSON. Response saved to nba-specimens.dwca.zip")
	@Produces(ZIP_CONTENT_TYPE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "collectionType", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "Crustacea", required = false) })
	public Response dwcaQuery(@ApiParam(value = "query string", required = true) @Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			StreamingOutput stream = new StreamingOutput() {

				public void write(OutputStream out) throws IOException
				{
					SpecimenDao dao = new SpecimenDao();
					try {
						dao.dwcaQuery(qs, out);
					} catch (Throwable e) {
						throw new RESTException(uriInfo, e);
					}
				}
			};
			ResponseBuilder response = Response.ok(stream);
			response.type(ZIP_CONTENT_TYPE);
			response.header("Content-Disposition", "attachment; filename=\"nba-specimens.dwca.zip\"");
			return response.build();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/getDataSet/{dataset}")
	@ApiOperation(value = "Download dataset as Darwin Core Archive File", response = Response.class, notes = "Available datasets can be queried with /specimen/dwca/getDataSetNames. Response saved to <datasetname>-<yyyymmdd>.dwca.zip")
	@Produces(ZIP_CONTENT_TYPE)
	public Response dwcaGetDataSet(
			@ApiParam(value = "name of dataset", required = true, defaultValue = "amphibia-and-reptilia") @PathParam("dataset") String name,
			@Context UriInfo uriInfo)
	{
		try {
			StreamingOutput stream = new StreamingOutput() {

				@Override
				public void write(OutputStream out) throws IOException
				{
					SpecimenDao dao = new SpecimenDao();
					try {
						dao.dwcaGetDataSet(name, out);
					} catch (Throwable e) {
						throw new RESTException(uriInfo, e);
					}
				}
			};
			ResponseBuilder response = Response.ok(stream);
			response.type(ZIP_CONTENT_TYPE);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fmt = "attachment; filename=\"%s-%s.dwca.zip\"";
			String hdr = String.format(fmt, name, sdf.format(new Date()));
			response.header("Content-Disposition", hdr);
			return response.build();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/getDataSetNames")
	@ApiOperation(value = "Retrieve the names of all available datasets", response = String[].class, notes = "Individual datasets can then be downloaded with /dwca/getDataSet/{dataset}")
	@Produces(JSON_CONTENT_TYPE)
	public String[] dwcaGetDataSetNames(@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.dwcaGetDataSetNames();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getNamedCollections")
	@ApiOperation(value = "Retrieve the names of all 'special collections' of specimens", response = String[].class, notes = "")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getNamedCollections(@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.getNamedCollections();
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getIdsInCollection/{name}")
	@ApiOperation(value = "Retrieve all ids within a 'special collection' of specimens", response = String[].class, notes = "Available collections can be queried with /getNamedCollections")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getIdsInCollection(
			@ApiParam(value = "name of dataset", required = true, defaultValue = "siebold") @PathParam("name") String name,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.getIdsInCollection(name);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/groupByScientificName")
	@ApiOperation(value = "Aggregates Taxon and Specimen documents according to their scientific names", response = QueryResult.class, notes = "Returns a list with ScientificNameGroups, which contain Taxon and Specimen documents that share a scientific name")
	@Produces(JSON_CONTENT_TYPE)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "collectionType", value = "Example query param", dataType = "string", paramType = "query", defaultValue = "Crustacea", required = false) })
	public QueryResult<ScientificNameGroup> groupByScientificName_GET(@Context UriInfo uriInfo)
	{
		try {
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			SpecimenDao dao = new SpecimenDao();
			return dao.groupByScientificName(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
