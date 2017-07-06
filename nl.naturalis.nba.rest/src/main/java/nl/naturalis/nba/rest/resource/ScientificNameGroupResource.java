package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.dao.DocumentType.SCIENTIFIC_NAME_GROUP;
import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;
import java.util.Set;

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

import nl.naturalis.nba.api.GroupByScientificNameQuerySpec;
import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.model.ScientificNameGroup_old;
import nl.naturalis.nba.dao.ScientificNameGroupDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpGroupByScientificNameQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

@Path("/names")
@Stateless
@LocalBean
@SuppressWarnings("static-method")
public class ScientificNameGroupResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(ScientificNameGroupResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public ScientificNameGroup_old find(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			ScientificNameGroup_old result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, SCIENTIFIC_NAME_GROUP, id);
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
	public ScientificNameGroup_old[] findByIds(@PathParam("ids") String ids, @Context UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			return dao.find(idArray);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<ScientificNameGroup_old> query_GET(@Context UriInfo uriInfo)
	{
		try {
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
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
	public QueryResult<ScientificNameGroup_old> query_POST_FORM(MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(form,
					uriInfo).build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
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
	public QueryResult<ScientificNameGroup_old> query_POST_JSON(GroupByScientificNameQuerySpec qs,
			@Context UriInfo uriInfo)
	{
		try {
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			QueryResult<ScientificNameGroup_old> result = dao.query(qs);
			return result;
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/querySpecial")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<ScientificNameGroup_old> querySpecial_GET(@Context UriInfo uriInfo)
	{
		try {
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			return dao.querySpecial(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/querySpecial")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public QueryResult<ScientificNameGroup_old> querySpecial_POST_FORM(
			MultivaluedMap<String, String> form, @Context UriInfo uriInfo)
	{
		try {
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(form,
					uriInfo).build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			return dao.querySpecial(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@POST
	@Path("/querySpecial")
	@Produces(JSON_CONTENT_TYPE)
	@Consumes(JSON_CONTENT_TYPE)
	public QueryResult<ScientificNameGroup_old> querySpecial_POST_JSON(GroupByScientificNameQuerySpec qs,
			@Context UriInfo uriInfo)
	{
		try {
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			return dao.querySpecial(qs);
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
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
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
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
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
			GroupByScientificNameQuerySpec qs = new HttpGroupByScientificNameQuerySpecBuilder(uriInfo)
					.build();
			QueryCondition[] conditions = null;
			if (qs.getConditions() != null && qs.getConditions().size() > 0) {
				conditions = qs.getConditions()
						.toArray(new QueryCondition[qs.getConditions().size()]);
			}
			ScientificNameGroupDao dao = new ScientificNameGroupDao();
			return dao.getDistinctValuesPerGroup(keyField, valuesField, conditions);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
