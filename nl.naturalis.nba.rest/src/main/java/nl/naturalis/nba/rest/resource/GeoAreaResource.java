package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;
import static nl.naturalis.nba.rest.util.ResourceUtil.stringAsJson;

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
import org.geojson.GeoJsonObject;

import nl.naturalis.nba.api.KeyValuePair;
import nl.naturalis.nba.api.model.GeoArea;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.GeoAreaDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

@Path("/geo")
@Stateless
@LocalBean
@SuppressWarnings("static-method")
public class GeoAreaResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(GeoAreaResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/find/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public GeoArea find(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			GeoArea result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.GEO_AREA, id);
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
	public GeoArea[] findByIds(@PathParam("ids") String ids, @Context UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			GeoAreaDao dao = new GeoAreaDao();
			return dao.find(idArray);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/query")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<GeoArea> query(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			GeoAreaDao dao = new GeoAreaDao();
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
			GeoAreaDao dao = new GeoAreaDao();
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
			GeoAreaDao dao = new GeoAreaDao();
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
			GeoAreaDao dao = new GeoAreaDao();
			return dao.getDistinctValues(field, qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getIdForLocality/{locality}")
	@Produces(JSON_CONTENT_TYPE)
	public String getIdForLocality(@PathParam("locality") String locality, @Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			String id = dao.getIdForLocality(locality);
			if (id == null) {
				String msg = String.format("No such locality: \"%s\"", locality);
				throw new HTTP404Exception(uriInfo, msg);
			}
			return stringAsJson(id);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getIdForIsoCode/{iso}")
	@Produces(JSON_CONTENT_TYPE)
	public String getIdForIsoCode(@PathParam("iso") String isoCode, @Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			String id = dao.getIdForIsoCode(isoCode);
			if (id == null) {
				String msg = String.format("No such ISO code: \"%s\"", isoCode);
				throw new HTTP404Exception(uriInfo, msg);
			}
			return stringAsJson(id);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getGeoJsonForId/{id}")
	@Produces(JSON_CONTENT_TYPE)
	public GeoJsonObject getGeoJsonForId(@PathParam("id") String id, @Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			GeoJsonObject json = dao.getGeoJsonForId(id);
			if (json == null) {
				String msg = String.format("No such ID: \"%s\"", id);
				throw new HTTP404Exception(uriInfo, msg);
			}
			return json;
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getLocalities")
	@Produces(JSON_CONTENT_TYPE)
	public List<KeyValuePair<String, String>> getLocalities(@Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			return dao.getLocalities();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/getIsoCodes")
	@Produces(JSON_CONTENT_TYPE)
	public List<KeyValuePair<String, String>> getIsoCodes(@Context UriInfo uriInfo)
	{
		try {
			GeoAreaDao dao = new GeoAreaDao();
			return dao.getIsoCodes();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
