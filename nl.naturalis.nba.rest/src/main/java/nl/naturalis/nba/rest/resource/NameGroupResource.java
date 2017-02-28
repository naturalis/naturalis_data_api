package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.dao.DocumentType.NAME_GROUP;
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

import nl.naturalis.nba.api.QueryCondition;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.NameGroup;
import nl.naturalis.nba.dao.NameGroupDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

@Path("/names")
@Stateless
@LocalBean
@SuppressWarnings("static-method")
public class NameGroupResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NameGroupResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public NameGroup find(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			NameGroupDao dao = new NameGroupDao();
			NameGroup result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, NAME_GROUP, id);
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
	public NameGroup[] findByIds(@PathParam("ids") String ids, @Context UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			NameGroupDao dao = new NameGroupDao();
			return dao.find(idArray);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<NameGroup> query_GET(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			NameGroupDao dao = new NameGroupDao();
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
	public QueryResult<NameGroup> query_POST_FORM(MultivaluedMap<String, String> form,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(form, uriInfo).build();
			NameGroupDao dao = new NameGroupDao();
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
	public QueryResult<NameGroup> query_POST_JSON(QuerySpec qs, @Context UriInfo uriInfo)
	{
		try {
			NameGroupDao dao = new NameGroupDao();
			return dao.query(qs);
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
			NameGroupDao dao = new NameGroupDao();
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
			NameGroupDao dao = new NameGroupDao();
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
				conditions = qs.getConditions()
						.toArray(new QueryCondition[qs.getConditions().size()]);
			}
			NameGroupDao dao = new NameGroupDao();
			return dao.getDistinctValuesPerGroup(keyField, valuesField, conditions);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
