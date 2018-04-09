package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.TEXT_CONTENT_TYPE;
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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.dao.MultiMediaObjectDao;

@Stateless
@LocalBean
@Api(value = "multimedia")
@Path("/multimedia")
@Produces({"application/json", "application/xml"})
public class MultiMediaObjectResource extends NbaResource<MultiMediaObject, MultiMediaObjectDao> {

  private static final Logger logger = LogManager.getLogger(MultiMediaObjectResource.class);

  MultiMediaObjectResource() {
    super(new MultiMediaObjectDao());
  }

  //@formatter:off
  @GET
  @Path("/find/{id}")
  @ApiOperation(
      value = "Find a multimedia document by id", 
      response = MultiMediaObject.class, 
      notes = "If found, returns a single multimedia document")
  @Produces(JSON_CONTENT_TYPE)
  public MultiMediaObject find(
      @ApiParam(
          value = "id of multimedia document", 
          required = true,
          defaultValue = "L.4169766_1307658521@BRAHMS") 
      @PathParam("id") String id,
      @Context UriInfo uriInfo) {
    return super.find(id, uriInfo);
  }

  @GET
  @Path("/findByIds/{ids}")
  @ApiOperation(
      value = "Find multimedia document by ids", 
      response = MultiMediaObject[].class, 
      notes = "Given multiple ids, returns a list of multimedia documents")
  @Produces(JSON_CONTENT_TYPE)
  public MultiMediaObject[] findByIds(
      @ApiParam(
          value = "ids of multiple multimedia documents, separated by comma", 
          required = true,
          defaultValue = "U.1475914_2059926060@BRAHMS,L.2454256_0837498402@BRAHMS",
          allowMultiple = true) 
      @PathParam("ids") String ids,
      @Context UriInfo uriInfo) {
    return super.findByIds(ids, uriInfo);
  }

  @GET
  @Path("/query")
  @ApiOperation(
      value = "Query for multimedia documents", 
      response = QueryResult.class, 
      notes = "Search for multimedia documents with query parameters or QuerySpec JSON string")
  @Produces(JSON_CONTENT_TYPE)
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "license", 
        value = "Example query param",
        dataType = "string",
        paramType = "query", 
        defaultValue = "CC0", required = false)})
  public QueryResult<MultiMediaObject> queryHttpGet(@Context UriInfo uriInfo) {
    return super.queryHttpGet(uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation(
      hidden = true, 
      value = "Query for multimedia documents", 
      response = QueryResult.class, 
      notes = "Search for multimedia documents with query parameters or QuerySpec JSON string")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public QueryResult<MultiMediaObject> queryHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.queryHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation(
      value = "Query for multimedia documents", 
      response = QueryResult.class, 
      notes = "Search for multimedia documents with query parameters or QuerySpec JSON string")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public QueryResult<MultiMediaObject> queryHttpPostJson(
      @ApiParam(value = "querySpec", required = false) QuerySpec qs, @Context UriInfo uriInfo) {
    return super.queryHttpPostJson(qs, uriInfo);
  }

  @GET
  @Path("/count")
  @ApiOperation(
      value = "Get the number of multimedia documents matching a given condition", 
      response = long.class, 
      notes = "Conditions given as query parameters or QuerySpec JSON")
  @Produces(JSON_CONTENT_TYPE)
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "sourceSystem.code", 
        value = "Example query param",
        dataType = "string", 
        paramType = "query", 
        defaultValue = "BRAHMS", 
        required = false)})
  public long countHttpGet(@Context UriInfo uriInfo) {
    return super.countHttpGet(uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(
      hidden = true,
      value = "Get the number of multimedia documents matching a given condition", 
      response = long.class,
      notes = "Conditions given as query parameters or QuerySpec JSON")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public long countHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.countHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(
      value = "Get the number of multimedia documents matching a given condition",
      response = long.class, 
      notes = "Conditions given as query parameters or QuerySpec JSON")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(JSON_CONTENT_TYPE)
  public long countHttpPostJson(
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    return super.countHttpPostJson(qs, uriInfo);
  }

  @GET
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field",
      response = long.class, 
      notes = "")
  @Produces(TEXT_CONTENT_TYPE)
  public long countDistinctValuesHttpGet(
      @ApiParam(value = "Name of field in taxon object", required = true, defaultValue = "defaultClassification.family") 
      @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field", 
      response = long.class, 
      notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(TEXT_CONTENT_TYPE)
  public long countDistinctValuesHttpPost(
      @ApiParam(value = "Name of field in the multimedia object", required = true, defaultValue = "identifications.defaultClassification.family") @PathParam("field") String field,
      @ApiParam(value = "Query object in POST form", required = false) MultivaluedMap<String, String> form,  
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field", 
      response = long.class, 
      notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(TEXT_CONTENT_TYPE)
  public long countDistinctValuesHttpJson(
      @ApiParam(value = "Name of field in the multimedia object", required = true, defaultValue = "identifications.defaultClassification.family") @PathParam("field") String field,
      @ApiParam(value = "QuerySpec JSON", required = false) QuerySpec qs, 
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpPostJson(field, qs, uriInfo);
  }

  @GET
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of field values that exist per the given field to group by",
      response = Map.class, 
      notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpGet(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "collectionType") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "identifications.typeStatus") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpGet(group, field, uriInfo);
  }

  @POST
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of field values that exist per the given field to group by", 
      response = Map.class, 
      notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostForm(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "collectionType") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "identifications.typeStatus") @PathParam("field") String field,
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpPostForm(group, field, form, uriInfo);
  }

  @POST
  @Path("/countDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Count the distinct number of field values that exist per the given field to group by", 
      response = Map.class, 
      notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> countDistinctValuesPerGroupHttpPostJson(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "collectionType") @PathParam("group") String group,
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "identifications.typeStatus") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValuesPerGroup/" + group + "/" + field);
    return super.countDistinctValuesPerGroupHttpPostJson(group, field, qs, uriInfo);
  }

  @GET
  @Path("/getDistinctValues/{field}")
  @ApiOperation(
      value = "Get all different values that can be found for one field",
      response = Map.class,
      notes = "A list of all fields for multimedia documents can be retrieved with /metadata/getFieldInfo")
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpGet(
      @ApiParam(value = "field", required = true, defaultValue = "gatheringEvents.worldRegion") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(
      value = "Get all different values that exist for a field", 
      response = Map.class,
      notes = "A list of all fields for multimedia documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostForm(
      @ApiParam(value = "name of field in a multimedia object", required = true, defaultValue = "gatheringEvents.worldRegion") @PathParam("field") String field,
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(
      value = "Get all different values that exist for a field", 
      response = Map.class,
      notes = "A list of all fields for multimedia documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostJson(
      @ApiParam(value = "name of field in a multimedia object", required = true, defaultValue = "gatheringEvents.worldRegion") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpPostJson(field, qs, uriInfo);
  }
  
  @GET
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Get all distinct values (and their document count) for the field given divided per distinct value of the field to group by", 
      response = List.class, 
      notes = "")
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroupHttpGet(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "identifications.scientificName.genusOrMonomial") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "collectionType") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpGet(group, field, uriInfo);
  }
  
  @POST
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Get all distinct values (and their document count) for the field given divided per distinct value of the field to group by", 
      response = List.class, 
      notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroupHttpPostForm(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "identifications.scientificName.genusOrMonomial") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "collectionType") @PathParam("field") String field,
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpPost(group, field, form, uriInfo);
  }
  
  @POST
  @Path("/getDistinctValuesPerGroup/{group}/{field}")
  @ApiOperation(
      value = "Get all distinct values (and their document count) for the field given divided per distinct value of the field to group by", 
      response = List.class, 
      notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public List<Map<String, Object>> getDistinctValuesPerGroup(
      @ApiParam(value = "name of field in the multimedia object you want to group by", required = true, defaultValue = "identifications.scientificName.genusOrMonomial") @PathParam("group") String group,
      @ApiParam(value = "name of field in the multimedia object", required = true, defaultValue = "collectionType") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpJson(group, field, qs, uriInfo);
  }
  //@formatter:on

}
