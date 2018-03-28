package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.TEXT_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;
import java.util.List;
import java.util.Map;
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
import org.geojson.GeoJsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.dao.GeoAreaDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;

@Stateless
@LocalBean
@Api(value = "geo")
@Path("/geo")
@Produces({"application/json", "application/xml"})
@SuppressWarnings("static-method")
public class GeoAreaResource extends NbaResource<GeoArea, GeoAreaDao> {

  @SuppressWarnings("unused")
  private static final Logger logger = LogManager.getLogger(GeoAreaResource.class);

  GeoAreaResource() {
    super(new GeoAreaDao());
  }

  @GET
  @Path("/find/{id}")
  @ApiOperation(value = "Find a GEO area by id", response = GeoArea.class,
      notes = "Returns a GEO object containing a GEO json polygon")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "id not found")})
  @Produces(JSON_CONTENT_TYPE)
  public GeoArea find(@ApiParam(value = "id of geo area", required = true,
      defaultValue = "1003937@GEO") @PathParam("id") String id, @Context UriInfo uriInfo) {
    return super.find(id, uriInfo);
  }

  @GET
  @Path("/findByIds/{ids}")
  @ApiOperation(value = "Find geo areas by ids", response = GeoArea[].class,
      notes = "Given multiple ids, returns a list of geo area objects")
  @Produces(JSON_CONTENT_TYPE)
  public GeoArea[] findByIds(@ApiParam(value = "ids of multiple geo areas, separated by comma",
      required = true, defaultValue = "1003937@GEO,1004048@GEO",
      allowMultiple = true) @PathParam("ids") String ids, @Context UriInfo uriInfo) {
    return super.findByIds(ids, uriInfo);
  }

  @GET
  @Path("/query")
  @ApiOperation(value = "Query for geo areas", response = QueryResult.class,
      notes = "Query on searchable fields to retrieve matching geo areas")
  @Produces(JSON_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "locality", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "Belgium", required = false)})
  public QueryResult<GeoArea> queryHttpGet(@Context UriInfo uriInfo) {
    return super.queryHttpGet(uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation(value = "Query for geo areas", response = QueryResult.class,
      notes = "Query on searchable fields to retrieve matching geo areas")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @ApiImplicitParams({@ApiImplicitParam(name = "locality", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "Belgium", required = false)})
  public QueryResult<GeoArea> queryHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.queryHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation(value = "Query for geo areas", response = QueryResult.class,
      notes = "Query on searchable fields to retrieve matching geo areas")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(JSON_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "locality", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "Belgium", required = false)})
  public QueryResult<GeoArea> queryHttpPostJson(
      @ApiParam(value = "querySpec", required = false) QuerySpec qs, @Context UriInfo uriInfo) {
    return super.queryHttpPostJson(qs, uriInfo);
  }

  @GET
  @Path("/count")
  @ApiOperation(value = "Get the number of geo areas matching a condition", response = long.class,
      notes = "Conditions given as query string")
  @Produces(TEXT_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "areaType", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "Country", required = false)})
  public long countHttpGet(@Context UriInfo uriInfo) {
    return super.countHttpGet(uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(hidden = true, value = "Get the number of geo areas matching a condition",
      response = long.class, notes = "Conditions given in POST body")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(TEXT_CONTENT_TYPE)
  public long countHttpPostForm(@ApiParam(value = "query object in POST form",
      required = false) MultivaluedMap<String, String> form, @Context UriInfo uriInfo) {
    return super.countHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(value = "Get the number of geo areas matching a condition", response = long.class,
      notes = "Conditions given as querySpec JSON")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(TEXT_CONTENT_TYPE)
  public long countHttpPostJson(
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    return super.countHttpPostJson(qs, uriInfo);
  }

  @GET
  @Path("/countDistinctValues/{field}")
  @ApiOperation(value = "Count the distinct number of values that exist for a given field",
      response = Map.class, notes = "")
  @Produces(TEXT_CONTENT_TYPE)
  public long countDistinctValuesHttpGet(
      @ApiParam(value = "name of field in taxon object", required = true, defaultValue = "defaultClassification.family") 
      @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(value = "Count the distinct number of values that exist for a given field", response = Map.class, notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(TEXT_CONTENT_TYPE)
  public Long countDistinctValuesHttpPost(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "identifications.defaultClassification.family") @PathParam("field") String field,
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,  
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(value = "Count the distinct number of values that exist for a given field", response = Map.class, notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(TEXT_CONTENT_TYPE)
  public Long countDistinctValuesHttpJson(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "identifications.defaultClassification.family") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs, 
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpPostJson(field, qs, uriInfo);
  }

  @GET
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(value = "Count the distinct number of group values that exist per the given field",
      response = Map.class, notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpGet(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group, 
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpGet(group, field, uriInfo);
  }

  @POST
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(value = "Count the distinct number of group values that exist per the given field", response = Map.class, notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostForm(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group, 
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpPostForm(group, field, form, uriInfo);
  }

  @POST
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(value = "Count the distinct number of group values that exist per the given field", response = Map.class, notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostJson(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpPostJson(group, field, qs, uriInfo);
  }

  @GET
  @Path("/getDistinctValues/{field}")
  @ApiOperation(value = "Get all different values that exist for a field", response = Map.class,
      notes = "A list of all fields for geo area documents can be retrieved with /metadata/getFieldInfo")
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpGet(
      @ApiParam(value = "name of field in geo area object", required = true, defaultValue = "locality") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(value = "Get all different values that exist for a field", response = Map.class,
      notes = "A list of all fields for geo area documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostForm(
      @ApiParam(value = "name of field in a geo area object", required = true, defaultValue = "locality") @PathParam("field") String field,
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(value = "Get all different values that exist for a field", response = Map.class,
      notes = "A list of all fields for geo area documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostJson(
      @ApiParam(value = "name of field in a geo area object", required = true, defaultValue = "locality") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpPostJson(field, qs, uriInfo);
  }
  
  @GET
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of group values that exist per the given field", 
      response = List.class, 
      notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroupHttpGet(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group, 
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpGet(group, field, uriInfo);
  }
  
  @POST
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of group values that exist per the given field", 
      response = List.class, 
      notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroupHttpPostForm(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group, 
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpPost(group, field, form, uriInfo);
  }
  
  @POST
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of group values that exist per the given field", 
      response = List.class, 
      notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroup(
      @ApiParam(value = "name of field in the geo area object", required = true, defaultValue = "areaType") @PathParam("field") String field,
      @ApiParam(value = "name of group in the geo area object", required = true, defaultValue = "locality") @PathParam("group") String group,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpJson(group, field, qs, uriInfo);
  }
  
  @GET
  @Path("/getGeoJsonForLocality/{locality}")
  @ApiOperation(value = "Retrieve a GeoJson object for a given locality", response = GeoArea.class, notes = "Returns a GeoJson polygon")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "locality not found")})
  @Produces(JSON_CONTENT_TYPE)
  public GeoJsonObject getGeoJsonForLocality(
      @PathParam("locality") String locality,
      @Context UriInfo uriInfo) {
    try {
      GeoAreaDao dao = new GeoAreaDao();
      GeoJsonObject json = dao.getGeoJsonForLocality(locality);
      if (json == null) {
        String msg = String.format("No such locality: \"%s\"", locality);
        throw new HTTP404Exception(uriInfo, msg);
      }
      return json;
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

}
