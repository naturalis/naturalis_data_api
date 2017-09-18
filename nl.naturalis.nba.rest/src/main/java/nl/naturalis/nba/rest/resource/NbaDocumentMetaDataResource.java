package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.dao.NbaDocumentMetaDataDao;

public abstract class NbaDocumentMetaDataResource<T extends NbaDocumentMetaDataDao<? extends IDocumentObject>> {

	private T dao;
	
	NbaDocumentMetaDataResource(T dao){
		this.dao = dao;
	}
	
	public Object getSettings(String name, UriInfo uriInfo)
	{
		try {
			NbaSetting setting = NbaSetting.parse(name);
			return dao.getSetting(setting);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
