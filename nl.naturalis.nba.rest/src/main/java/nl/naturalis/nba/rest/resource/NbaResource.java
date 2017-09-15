package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.NbaDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;
import nl.naturalis.nba.rest.util.HttpQuerySpecBuilder;
import nl.naturalis.nba.utils.StringUtil;

public abstract class NbaResource<T extends IDocumentObject, U extends NbaDao<T>> {
	
	U dao; // NbaResource, e.g. SpecimenResource, TaxonResource ...
	
	NbaResource(U dao) {
		this.dao = dao;
	}
	
	public T find(String id, UriInfo uriInfo)
	{
		try {
			T result = dao.find(id);
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.SPECIMEN, id);
			}
			return result;
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
	
	public T[] findByIds(String ids, UriInfo uriInfo)
	{
		try {
			String[] idArray = StringUtil.split(ids, ",");
			return dao.findByIds(idArray);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}
	
	public QueryResult<T> query_GET(UriInfo uriInfo)
	{
		try {
			QuerySpec qs = new HttpQuerySpecBuilder(uriInfo).build();
			return dao.query(qs);
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}



}
