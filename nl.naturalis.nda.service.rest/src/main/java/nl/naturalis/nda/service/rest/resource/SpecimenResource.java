package nl.naturalis.nda.service.rest.resource;

import java.io.File;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.elasticsearch.dao.dao.BioportalSpecimenDao;
import nl.naturalis.nda.elasticsearch.dao.dao.SpecimenDao;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.service.rest.util.NDA;
import nl.naturalis.nda.service.rest.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/specimen")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class SpecimenResource {

	private static final Logger logger = LoggerFactory.getLogger(SpecimenResource.class);

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;
	
	@GET
	@Path("/exists/{id}")
	@Produces("text/plain;charset=UTF-8")	
	public boolean exists(@PathParam("id") String unitID) {
		SpecimenDao dao = registry.getSpecimenDao(null);
		return dao.exists(unitID);		
	}


	@GET
	@Path("/get-specimen")
	@Produces(ResourceUtil.JSON_CONTENT_TYPE)
	public SearchResultSet<Specimen> getSpecimenDetail(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		logger.debug("getSpecimenDetail");
		String sessionId = request.getSession().getId();
		SearchResultSet<Specimen> result = null;
		try {
			String unitID = uriInfo.getQueryParameters().getFirst("unitID");
			String baseUrl = uriInfo.getBaseUri().toString();
			SpecimenDao dao = registry.getSpecimenDao(baseUrl);
			result = dao.getSpecimenDetail(unitID, sessionId);
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
		if (result == null) {
			throw ResourceUtil.handleError(uriInfo, Status.NOT_FOUND);
		}
		ResourceUtil.doAfterDao(result, uriInfo, false);
		return result;
	}


	@GET
	@Path("/get-specimen-within-result-set")
	@Produces(ResourceUtil.JSON_CONTENT_TYPE)
	public SearchResultSet<Specimen> getSpecimenDetailWithinResultSetGET(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("getSpecimenDetailWithinResultSetGET");
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			SearchResultSet<Specimen> result = dao.getSpecimenDetailWithinSearchResult(params);
			ResourceUtil.doAfterDao(result, uriInfo, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}


	@POST
	@Path("/get-specimen-within-result-set")
	@Produces(ResourceUtil.JSON_CONTENT_TYPE)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SearchResultSet<Specimen> getSpecimenDetailWithinResultSetPOST(MultivaluedMap<String, String> form, @Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("getSpecimenDetailWithinResultSetPOST");
			QueryParams params = new QueryParams(form);
			params.addParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			SearchResultSet<Specimen> result = dao.getSpecimenDetailWithinSearchResult(params);
			ResourceUtil.doAfterDao(result, uriInfo, form, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, form, t);
		}
	}


	@GET
	@Path("/search")
	@Produces(ResourceUtil.JSON_CONTENT_TYPE)
	public SearchResultSet<Specimen> searchGET(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("searchGET");
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			SearchResultSet<Specimen> result = dao.specimenSearch(params);
			ResourceUtil.doAfterDao(result, uriInfo, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}


	@POST
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SearchResultSet<Specimen> searchPOST(MultivaluedMap<String, String> form, @Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("searchPOST");
			QueryParams params = new QueryParams(form);
			params.addParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			SearchResultSet<Specimen> result = dao.specimenSearch(params);
			ResourceUtil.doAfterDao(result, uriInfo, form, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, form, t);
		}
	}

	@GET
	@Path("/search/dwca")
	@Produces("application/zip")
	public File searchDwca(@Context UriInfo uriInfo)
	{
		try {
			logger.debug("search/dwca");
			String collection = uriInfo.getQueryParameters().getFirst("collection");
			if(collection == null) {
				throw new WebApplicationException("Missing required parameter: collection");
			}
			String outputDir = registry.getNDA().getConfig().required("nda.export.output.dir");
			String path = outputDir + "/dwca/zip/" + collection + ".zip";
			File f = new File(path);
			if(!f.isFile()) {
				logger.error("No such file: " + f.getAbsolutePath());
				throw new WebApplicationException("The requested collection does not exist, or no DwCA file is generated for it yet", 404);
			}
			return f;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}

	@GET
	@Path("/name-search")
	@Produces(ResourceUtil.JSON_CONTENT_TYPE)
	public ResultGroupSet<Specimen, String> nameSearchGET(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("nameSearchGET");
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);
			ResourceUtil.doAfterDao(result, uriInfo, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}


	@POST
	@Path("/name-search")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public ResultGroupSet<Specimen, String> nameSearchPOST(MultivaluedMap<String, String> form, @Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("nameSearchPOST");
			QueryParams params = new QueryParams(form);
			params.addParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalSpecimenDao dao = registry.getBioportalSpecimenDao(baseUrl);
			ResultGroupSet<Specimen, String> result = dao.specimenNameSearch(params);
			ResourceUtil.doAfterDao(result, uriInfo, form, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, form, t);
		}
	}

}
