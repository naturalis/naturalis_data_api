package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.dao.DocumentType;
import nl.naturalis.nba.dao.NbaDao;
import nl.naturalis.nba.rest.exception.HTTP404Exception;

public abstract class NbaResource<T, U> {
	
	T docObj;
	U dao;
	
	// NbaDao<Specimen>
	
	NbaResource(U dao) {
		this.dao = dao;
	}
	
	@SuppressWarnings("static-method")
	public T find(String id, UriInfo uriInfo)
	{
		try {
			// U dao = new SpecimenDao();
			T result = (T) ((NbaDao<Specimen>) dao).find(id); // ????
			if (result == null) {
				throw new HTTP404Exception(uriInfo, DocumentType.SPECIMEN, id);
			}
			return result;
		} catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
