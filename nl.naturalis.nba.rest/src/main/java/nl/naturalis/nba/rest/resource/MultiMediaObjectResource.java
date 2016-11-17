package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.List;
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

import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.MultiMediaObjectDao;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;

@SuppressWarnings("static-method")
@Path("/multimedia")
@Stateless
@LocalBean
public class MultiMediaObjectResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(MultiMediaObjectResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public MultiMediaObject find(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			MultiMediaObject result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.TAXON, id);
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
	public MultiMediaObject[] findByIds(@PathParam("ids") String ids, @Context UriInfo uriInfo)
	{
		try {
			String[] idArray = JsonUtil.deserialize(ids, String[].class);
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.find(idArray);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<MultiMediaObject> query(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.query(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/queryData")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<Map<String, Object>> queryData(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
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
			TaxonDao dao = new TaxonDao();
			return dao.count(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getDistinctValues/{field}")
	@Produces(JSON_CONTENT_TYPE)
	public List<KeyValuePair<String, Long>> getDistinctValues(@PathParam("field") String field,
			@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			MultiMediaObjectDao dao = new MultiMediaObjectDao();
			return dao.getDistinctValues(field, qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
}
