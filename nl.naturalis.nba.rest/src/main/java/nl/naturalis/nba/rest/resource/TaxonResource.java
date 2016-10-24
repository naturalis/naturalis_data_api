package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.JSON_CONTENT_TYPE;
import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.NoSuchDataSetException;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QueryResult;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.dao.TaxonDao;
import nl.naturalis.nba.rest.exception.HTTP400Exception;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.exception.RESTException;
import nl.naturalis.nba.rest.util.UrlQuerySpecBuilder;

@SuppressWarnings("static-method")
@Path("/taxon")
@Stateless
@LocalBean
public class TaxonResource {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(TaxonResource.class);

	@EJB
	Registry registry;

	@GET
	@Path("/queryRaw")
	@Produces(JSON_CONTENT_TYPE)
	public QueryResult<Map<String, Object>> queryRaw(@Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new UrlQuerySpecBuilder(uriInfo).build();
			TaxonDao dao = new TaxonDao();
			return dao.queryRaw(qs);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/dwca/query/{querySpec}")
	@Produces("application/zip")
	public Response dwcaQuery(@PathParam("querySpec") String json, @Context UriInfo uriInfo)
	{
		try {
			QuerySpec qs = JsonUtil.deserialize(json, QuerySpec.class);
			StreamingOutput stream = new StreamingOutput() {

				@Override
				public void write(OutputStream out) throws IOException
				{
					TaxonDao dao = new TaxonDao();
					try {
						dao.dwcaQuery(qs, out);
					}
					catch (InvalidQueryException e) {
						throw new HTTP400Exception(uriInfo, e.getMessage());
					}
					catch (Throwable e) {
						throw new RESTException(uriInfo, e);
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
					TaxonDao dao = new TaxonDao();
					try {
						dao.dwcaGetDataSet(name, out);
					}
					catch (NoSuchDataSetException e) {
						throw new HTTP404Exception(uriInfo, e.getMessage());
					}
					catch (Throwable e) {
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
			TaxonDao dao = new TaxonDao();
			return dao.dwcaGetDataSetNames();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
