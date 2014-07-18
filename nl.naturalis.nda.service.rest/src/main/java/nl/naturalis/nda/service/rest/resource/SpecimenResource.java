package nl.naturalis.nda.service.rest.resource;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nda.domain.Specimen;
import nl.naturalis.nda.domain.SpecimenSearchResult;
import nl.naturalis.nda.ejb.service.SpecimenService;
import nl.naturalis.nda.elasticsearch.dao.dao.SpecimenDao;
import nl.naturalis.nda.elasticsearch.dao.util.QueryParams;
import nl.naturalis.nda.service.rest.exception.InvalidQueryException;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.FilteredQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

@Path("/specimen")
@Stateless
@LocalBean
/* only here so @EJB injection works in JBoss AS; remove when possible */
public class SpecimenResource {

	@EJB
	SpecimenService service;

	@EJB
	Registry registry;


	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public SpecimenSearchResult search(@Context UriInfo request)
	{
		SpecimenDao dao = new SpecimenDao(registry.getESClient(), "nda");
		QueryParams params = new QueryParams(request.getQueryParameters());
		SpecimenSearchResult result = dao.listSpecimens(params);
		return result;		
	}


	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Specimen getSpecimen(@PathParam("id") String id)
	{
		Specimen specimen = new Specimen();
		return specimen;
	}

}
