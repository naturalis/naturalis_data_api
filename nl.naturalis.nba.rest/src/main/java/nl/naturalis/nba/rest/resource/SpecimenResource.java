package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.QueryCondition;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.SpecimenDao;
import nl.naturalis.nba.rest.exception.HTTP400Exception;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.exception.RESTException;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

@SuppressWarnings("static-method")
@Path("/specimen")
@Stateless
@LocalBean
public class SpecimenResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public Specimen find(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			Specimen result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.SPECIMEN, id);
			}
			return result;
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/findByIds/{ids}")
	@Produces(JSON_CONTENT_TYPE)
	public Specimen[] findByIds(@PathParam("ids") String ids, @Context UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			SpecimenDao dao = new SpecimenDao();
			return dao.find(idArray);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/findByUnitID/{unitID}")
	@Produces(JSON_CONTENT_TYPE)
	public Specimen[] findByUnitID(@PathParam("unitID") String unitID, @Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.findByUnitID(unitID);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/exists/{unitID}")
	@Produces(JSON_CONTENT_TYPE)
	public boolean exists(@PathParam("unitID") String unitID, @Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.exists(unitID);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/save/specimen")
	@Produces(JSON_CONTENT_TYPE)
	public String save(@PathParam("specimen") String json, @Context UriInfo uriInfo)
	{
		return save(json, uriInfo, false);
	}

	@GET
	@Path("/save/specimen/immediate")
	@Produces(JSON_CONTENT_TYPE)
	public String saveImmediate(@PathParam("specimen") String json, @Context UriInfo uriInfo)
	{
		return save(json, uriInfo, true);
	}

	@GET
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<Specimen> query_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public QueryResult<Specimen> query_POST_FORM(MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public QueryResult<Specimen> query_POST_JSON(QuerySpec qs, @Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/queryData")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<Map<String, Object>> queryData_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.queryData(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/queryData")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public QueryResult<Map<String, Object>> queryData_POST_FORM(MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.queryData(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/queryData")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public QueryResult<Map<String, Object>> queryData_POST_JSON(QuerySpec qs,
			@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.queryData(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/count")
	@Produces(JSON_CONTENT_TYPE)
	public long count(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.count(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getDistinctValues/{field}")
	@Produces(JSON_CONTENT_TYPE)
	public Map<String, Long> getDistinctValues(@PathParam("field") String field,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.getDistinctValues(field, qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getDistinctValuesPerGroup/{keyField}/{valuesField}")
	@Produces(JSON_CONTENT_TYPE)
	public Map<Object, Set<Object>> getDistinctValuesPerGroup(
			@PathParam("keyField") String keyField, @PathParam("valuesField") String valuesField,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			QueryCondition[] conditions = null;
			if (qs.getConditions() != null && qs.getConditions().size() > 0) {
				conditions = qs.getConditions().toArray(new QueryCondition[qs.getConditions().size()]);
			}
			SpecimenDao dao = new SpecimenDao();
			return dao.getDistinctValuesPerGroup(keyField, valuesField, conditions);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getGroups/{groupByField}")
	@Produces(JSON_CONTENT_TYPE)
	public List<KeyValuePair<Object, Integer>> getGroups(
			@PathParam("groupByField") String groupByField, @Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			SpecimenDao dao = new SpecimenDao();
			return dao.getGroups(groupByField, qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/query")
	@Produces("application/zip")
	public Response dwcaQuery(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			StreamingOutput stream = new StreamingOutput() {

				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException
				{
					SpecimenDao dao = new SpecimenDao();
					try {
						dao.dwcaQuery(qs, new ZipOutputStream(out));
					}
					catch (InvalidQueryException e) {
						throw new HTTP400Exception(uriInfo, e.getMessage());
					}
				}
			};
			ResponseBuilder response = Response.ok(stream);
			response.type("application/zip");
			response.header("Content-Disposition", "attachment; filename=\"nba.dwca.zip\"");
			return response.build();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/dataset/{dataset}")
	@Produces("application/zip")
	public Response dwcaGetDataSet(@PathParam("dataset") String name, @Context UriInfo uriInfo)
	{
		try {
			StreamingOutput stream = new StreamingOutput() {

				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException
				{
					SpecimenDao dao = new SpecimenDao();
					try {
						dao.dwcaGetDataSet(name, new ZipOutputStream(out));
					}
					catch (InvalidQueryException e) {
						throw new WebApplicationException(e);
					}
				}
			};
			ResponseBuilder response = Response.ok(stream);
			response.type("application/zip");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String fmt = "attachment; filename=\"%s-%s.zip\"";
			String hdr = String.format(fmt, name, sdf.format(new Date()));
			response.header("Content-Disposition", hdr);
			return response.build();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/getDataSetNames")
	@Produces(JSON_CONTENT_TYPE)
	public String[] dwcaGetDataSetNames(@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.dwcaGetDataSetNames();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getNamedCollections")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getNamedCollections(@Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.getNamedCollections();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getIdsInCollection/{name}")
	@Produces(JSON_CONTENT_TYPE)
	public String[] getIdsInCollection(@PathParam("name") String name, @Context UriInfo uriInfo)
	{
		try {
			SpecimenDao dao = new SpecimenDao();
			return dao.getIdsInCollection(name);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	private static String save(String json, UriInfo uriInfo, boolean immediate)
	{
		try {
			String host = uriInfo.getBaseUri().getHost();
			if (!host.equals("localhost") && !host.equals("127.0.0.1")) {
				String msg = "Method not allowed for remote clients";
				throw new RESTException(uriInfo, Status.FORBIDDEN, msg);
			}
			Specimen specimen = JsonUtil.deserialize(json, Specimen.class);
			SpecimenDao dao = new SpecimenDao();
			return dao.save(specimen, immediate);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
}
