package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.TEXT_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.ZIP_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.ws.rs.WebApplicationException;
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
import nl.naturalis.nba.api.GroupByScientificNameQueryResult;
import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.Taxon;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.rest.exception.RESTException;
import nl.naturalis.nba.rest.util.HttpGroupByScientificNameQuerySpecBuilder;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;

@Stateless
@LocalBean
@Api(value = "taxon")
@Path("/taxon")
@Produces({"application/json", "application/xml"})
@SuppressWarnings("static-method")
public class TaxonResource extends NbaResource<Taxon, TaxonDao> {

  private static final Logger logger = LogManager.getLogger(TaxonResource.class);

  TaxonResource() {
    super(new TaxonDao());
  }

  //@formatter:off
  @GET
  @Path("/find/{id}")
  @ApiOperation(
      value = "Find a taxon by id", 
      response = Taxon.class,
      notes = "If found, returns a single taxon")
  @ApiResponses(value = {@ApiResponse(code = 404, message = "id not found")})
  @Produces(JSON_CONTENT_TYPE)
  public Taxon find(@ApiParam(value = "id of taxon", required = true,
      defaultValue = "21941298@COL") @PathParam("id") String id, @Context UriInfo uriInfo) {
    return super.find(id, uriInfo);
  }

  @GET
  @Path("/findByIds/{ids}")
  @ApiOperation(
      value = "Find taxa by ids", 
      response = Taxon[].class,
      notes = "Given multiple ids, returns a list of taxa")
  @Produces(JSON_CONTENT_TYPE)
  public Taxon[] findByIds(@ApiParam(value = "ids of multiple taxa, separated by comma",
      required = true, defaultValue = "21941298@COL,21941294@COL",
      allowMultiple = true) @PathParam("ids") String ids, @Context UriInfo uriInfo) {
    return super.findByIds(ids, uriInfo);
  }

  @GET
  @Path("/query")
  @ApiOperation(
      value = "Query for taxa", 
      response = QueryResult.class,
      notes = "Search for taxa (GET) using query parameters or a querySpec JSON")
  @Produces(JSON_CONTENT_TYPE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "defaultClassification.genus", value = "Example query param",
          dataType = "string", paramType = "query", defaultValue = "Epinicium", required = false)})
  public QueryResult<Taxon> queryHttpGet(@Context UriInfo uriInfo) {
    return super.queryHttpGet(uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation( 
      value = "Query for taxa", 
      response = QueryResult.class,
      notes = "Search for taxa (POST) using query parameters or a querySpec JSON")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public QueryResult<Taxon> queryHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.queryHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/query")
  @ApiOperation(
      value = "Query for taxa", 
      response = QueryResult.class,
      notes = "Search for taxa (POST) using query parameters or a querySpec JSON")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(JSON_CONTENT_TYPE)
  public QueryResult<Taxon> queryHttpPostJson(
      @ApiParam(value = "querySpec", required = false) QuerySpec qs, @Context UriInfo uriInfo) {
    return super.queryHttpPostJson(qs, uriInfo);
  }

  @GET
  @Path("/count")
  @ApiOperation(
      value = "Get the number of taxa matching a given condition", 
      response = long.class,
      notes = "Conditions given as query parameters or a querySpec JSON")
  @Produces(TEXT_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "sourceSystem.code", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "COL", required = false)})
  public long countHttpGet(@Context UriInfo uriInfo) {
    return super.countHttpGet(uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(
      value = "Get the number of taxa matching a given condition",
      response = long.class, 
      notes = "Conditions given as query parameters or a querySpec JSON")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(TEXT_CONTENT_TYPE)
  public long countHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.countHttpPostForm(form, uriInfo);
  }

  @POST
  @Path("/count")
  @ApiOperation(
      value = "Get the number of taxa matching a given condition", 
      response = long.class,
      notes = "Conditions given as query parameters or a querySpec JSON")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(TEXT_CONTENT_TYPE)
  public long countHttpPostJson(
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    return super.countHttpPostJson(qs, uriInfo);
  }
  
  @GET
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field", 
      response = Map.class, 
      notes = "")
  @Produces(TEXT_CONTENT_TYPE)
  public long countDistinctValuesHttpGet(
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "acceptedName.genusOrMonomial") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field", 
      response = Map.class, 
      notes = "")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(TEXT_CONTENT_TYPE)
  public Long countDistinctValuesHttpPost(
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "acceptedName.genusOrMonomial") @PathParam("field") String field,
      @ApiParam(value = "query object in POST form", required = false) MultivaluedMap<String, String> form,  
      @Context UriInfo uriInfo) {
    logger.info("countDistinctValues/" + field);
    return super.countDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/countDistinctValues/{field}")
  @ApiOperation(
      value = "Count the distinct number of values that exist for a given field", 
      response = Map.class, 
      notes = "")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(TEXT_CONTENT_TYPE)
  public Long countDistinctValuesHttpJson(
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "acceptedName.genusOrMonomial") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs, 
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.genus") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.genus") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.genus") @PathParam("group") String group,
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
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
      notes = "A list of all fields for taxon documents can be retrieved with /metadata/getFieldInfo")
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpGet(
      @ApiParam(value = "name of field in a taxon object", required = true, defaultValue = "defaultClassification.family") @PathParam("field") String field,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpGet(field, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(
      value = "Get all different values that exist for a field", 
      response = Map.class,
      notes = "A list of all fields for taxon documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostForm(
      @ApiParam(value = "name of field in a taxon object", required = true, defaultValue = "defaultClassification.family") @PathParam("field") String field,
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    return super.getDistinctValuesHttpPostForm(field, form, uriInfo);
  }

  @POST
  @Path("/getDistinctValues/{field}")
  @ApiOperation(
      value = "Get all different values that exist for a field", 
      response = Map.class,
      notes = "A list of all fields for taxon documents can be retrieved with /metadata/getFieldInfo")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(JSON_CONTENT_TYPE)
  public Map<String, Long> getDistinctValuesHttpPostJson(
      @ApiParam(value = "name of field in a taxon object", required = true, defaultValue = "defaultClassification.family") @PathParam("field") String field,
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.family") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.family") @PathParam("group") String group, 
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
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
      @ApiParam(value = "name of field in the taxon object you want to group by", required = true, defaultValue = "defaultClassification.family") @PathParam("group") String group,
      @ApiParam(value = "name of field in the taxon object", required = true, defaultValue = "sourceSystem.code") @PathParam("field") String field,
      @ApiParam(value = "querySpec JSON", required = false) QuerySpec qs,
      @Context UriInfo uriInfo) {
    logger.info("getDistinctValuesPerGroup/" + group + "/" + field);
    return super.getDistinctValuesPerGroupHttpJson(group, field, qs, uriInfo);
  }

  @GET
  @Path("/dwca/query")
  @ApiOperation(
      value = "Dynamic download service: Query for taxa and return result as Darwin Core Archive File",
      response = Response.class,
      notes = "Query with query parameters or querySpec JSON. Response saved to nba-taxa.dwca.zip")
  @Produces(ZIP_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "sourceSystem.code", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "COL", required = false)})
  public Response dwcaQueryHttpGet(@Context UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
      StreamingOutput stream = new StreamingOutput() {

        public void write(OutputStream out) throws IOException {
          TaxonDao dao = new TaxonDao();
          try {
            dao.dwcaQuery(qs, out);
          } catch (Throwable e) {
            throw new RESTException(uriInfo, e);
          }
        }
      };
      ResponseBuilder response = Response.ok(stream);
      response.type(ZIP_CONTENT_TYPE);
      response.header("Content-Disposition", "attachment; filename=\"nba-taxa.dwca.zip\"");
      return response.build();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @POST
  @Path("/dwca/query")
  @ApiOperation(
      value = "Dynamic download service: Query for taxa and return result as Darwin Core Archive File",
      response = Response.class,
      notes = "Query with query parameters or querySpec JSON. Response saved to nba-taxa.dwca.zip")
  @Produces(ZIP_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "sourceSystem.code", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "COL", required = false)})
  public Response dwcaQueryHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    try {
      QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
      StreamingOutput stream = new StreamingOutput() {

        public void write(OutputStream out) throws IOException {
          TaxonDao dao = new TaxonDao();
          try {
            dao.dwcaQuery(qs, out);
          } catch (Throwable e) {
            throw new RESTException(uriInfo, e);
          }
        }
      };
      ResponseBuilder response = Response.ok(stream);
      response.type(ZIP_CONTENT_TYPE);
      response.header("Content-Disposition", "attachment; filename=\"nba-taxa.dwca.zip\"");
      return response.build();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @POST
  @Path("/dwca/query")
  @ApiOperation(
      value = "Dynamic download service: Query for taxa and return result as Darwin Core Archive File",
      response = Response.class,
      notes = "Query with query parameters or querySpec JSON. Response saved to nba-taxa.dwca.zip")
  @Consumes(JSON_CONTENT_TYPE)
  @Produces(ZIP_CONTENT_TYPE)
  @ApiImplicitParams({@ApiImplicitParam(name = "sourceSystem.code", value = "Example query param",
      dataType = "string", paramType = "query", defaultValue = "COL", required = false)})
  public Response dwcaQueryHttpPostJson(
      @ApiParam(value = "querySpec", required = false) QuerySpec qs, @Context UriInfo uriInfo) {
    try {
      StreamingOutput stream = new StreamingOutput() {

        public void write(OutputStream out) throws IOException {
          TaxonDao dao = new TaxonDao();
          try {
            dao.dwcaQuery(qs, out);
          } catch (Throwable e) {
            throw new RESTException(uriInfo, e);
          }
        }
      };
      ResponseBuilder response = Response.ok(stream);
      response.type(ZIP_CONTENT_TYPE);
      response.header("Content-Disposition", "attachment; filename=\"nba-taxa.dwca.zip\"");
      return response.build();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/dwca/getDataSet/{dataset}")
  @ApiOperation(
      value = "Download dataset as Darwin Core Archive File", 
      response = Response.class,
      notes = "Available datasets can be queried with /taxon/dwca/getDataSetNames. Response saved to <datasetname>-<yyyymmdd>.dwca.zip")
  @Produces(ZIP_CONTENT_TYPE)
  public Response dwcaGetDataSet(@ApiParam(value = "name of dataset", required = true,
      defaultValue = "nsr") @PathParam("dataset") String name, @Context UriInfo uriInfo) {
    try {
      StreamingOutput stream = new StreamingOutput() {

        @Override
        public void write(OutputStream out) throws IOException, WebApplicationException {
          TaxonDao dao = new TaxonDao();
          try {
            dao.dwcaGetDataSet(name, out);
          } catch (Throwable e) {
            throw new RESTException(uriInfo, e);
          }
        }
      };
      ResponseBuilder response = Response.ok(stream);
      response.type("application/zip");
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
  @ApiOperation(
      value = "Retrieve the names of all available datasets", 
      response = String[].class,
      notes = "Individual datasets can then be downloaded with /dwca/getDataSet/{dataset}")
  @Produces(JSON_CONTENT_TYPE)
  public String[] dwcaGetDataSetNames(@Context UriInfo uriInfo) {
    try {
      TaxonDao dao = new TaxonDao();
      return dao.dwcaGetDataSetNames();
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @GET
  @Path("/groupByScientificName")
  @ApiOperation(
      value = "Aggregates Taxon and Specimen documents according to their scientific names",
      response = QueryResult.class,
      notes = "Returns a list with ScientificNameGroups, which contain Taxon and Specimen documents that share a scientific name")
  @Produces(JSON_CONTENT_TYPE)
  @ApiImplicitParams({
      @ApiImplicitParam(name = "defaultClassification.family", value = "Example query param",
          dataType = "string", paramType = "query", defaultValue = "Fabaceae", required = false)})
  public GroupByScientificNameQueryResult groupByScientificNameHttpGet(@Context UriInfo uriInfo) {
    try {
      GroupByScientificNameQuerySpec qs =
          new HttpGroupByScientificNameQuerySpecBuilder(uriInfo).build();
      TaxonDao dao = new TaxonDao();
      return dao.groupByScientificName(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @POST
  @Path("/groupByScientificName")
  @ApiOperation(
      value = "Aggregates Taxon and Specimen documents according to their scientific names",
      response = QueryResult.class,
      notes = "Returns a list with ScientificNameGroups, which contain Taxon and Specimen documents that share a scientific name")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  public GroupByScientificNameQueryResult groupByScientificNameHttpPostForm(
      @ApiParam(value = "POST payload", required = false) MultivaluedMap<String, String> form,
      @Context UriInfo uriInfo) {
    try {
      GroupByScientificNameQuerySpec qs =
          new HttpGroupByScientificNameQuerySpecBuilder(form, uriInfo).build();
      TaxonDao dao = new TaxonDao();
      return dao.groupByScientificName(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }

  @POST
  @Path("/groupByScientificName")
  @ApiOperation(
      value = "Aggregates Taxon and Specimen documents according to their scientific names",
      response = QueryResult.class,
      notes = "Returns a list with ScientificNameGroups, which contain Taxon and Specimen documents that share a scientific name")
  @Produces(JSON_CONTENT_TYPE)
  @Consumes(JSON_CONTENT_TYPE)
  public GroupByScientificNameQueryResult groupByScientificNameHttpPostJson(
      @ApiParam(value = "querySpec JSON", required = false) GroupByScientificNameQuerySpec qs,
      @Context UriInfo uriInfo) {
    try {
      TaxonDao dao = new TaxonDao();
      return dao.groupByScientificName(qs);
    } catch (Throwable t) {
      throw handleError(uriInfo, t);
    }
  }
  //@formatter:on
}
