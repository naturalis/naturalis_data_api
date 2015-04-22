package nl.naturalis.nda.service.rest.resource;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Taxon;
import nl.naturalis.nda.elasticsearch.dao.dao.BioportalTaxonDao;
import nl.naturalis.nda.elasticsearch.dao.dao.TaxonDao;
import nl.naturalis.nda.search.QueryParams;
import nl.naturalis.nda.search.ResultGroupSet;
import nl.naturalis.nda.search.SearchResultSet;
import nl.naturalis.nda.service.rest.util.NDA;
import nl.naturalis.nda.service.rest.util.ResourceUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/taxon")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class TaxonResource {

	private static final Logger logger = LoggerFactory.getLogger(TaxonResource.class);

	@EJB
	Registry registry;


	@GET
	@Path("/get-taxon")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetail(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		logger.debug("getTaxonDetail");
		SearchResultSet<Taxon> result = null;
		try {
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			TaxonDao dao = registry.getTaxonDao(baseUrl);
			result = dao.getTaxonDetail(params);
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
	@Path("/get-taxon-within-result-set")
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResultSet<Taxon> getTaxonDetailWithinResultSet(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("getTaxonDetailWithinResultSet");
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalTaxonDao dao = registry.getBioportalTaxonDao(baseUrl);
			SearchResultSet<Taxon> result = dao.getTaxonDetailWithinResultSet(params);
			ResourceUtil.doAfterDao(result, uriInfo, false);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, t);
		}
	}


	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public ResultGroupSet<Taxon, String> searchGET(@Context UriInfo uriInfo, @Context HttpServletRequest request)
	{
		try {
			logger.debug("searchGET");
			QueryParams params = new QueryParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalTaxonDao dao = registry.getBioportalTaxonDao(baseUrl);
			ResultGroupSet<Taxon, String> result = dao.taxonSearch(params);
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
	public ResultGroupSet<Taxon, String> searchPOST(@Context UriInfo uriInfo, MultivaluedMap<String, String> form, @Context HttpServletRequest request)
	{
		try {
			logger.debug("searchPOST");
			QueryParams params = new QueryParams(form);
			params.addParams(uriInfo.getQueryParameters());
			params.putSingle(NDA.SESSION_ID_PARAM, request.getSession().getId());
			String baseUrl = uriInfo.getBaseUri().toString();
			BioportalTaxonDao dao = registry.getBioportalTaxonDao(baseUrl);
			ResultGroupSet<Taxon, String> result = dao.taxonSearch(params);
			ResourceUtil.doAfterDao(result, uriInfo, form, true);
			return result;
		}
		catch (Throwable t) {
			throw ResourceUtil.handleError(uriInfo, form, t);
		}
	}

}
