package nl.naturalis.nba.rest.resource;

import static nl.naturalis.nba.rest.util.ResourceUtil.handleError;

import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.core.UriInfo;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.dao.NbaDocumentMetaDataDao;
import nl.naturalis.nba.utils.ConfigObject;

public abstract class NbaDocumentMetaDataResource<T extends NbaDocumentMetaDataDao<? extends IDocumentObject>> {

	private T dao;
	
	@EJB
	Registry registry;
	
	NbaDocumentMetaDataResource(T dao)
	{
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

	public Map<NbaSetting, Object> getSettings(UriInfo uriInfo)
	{
		try {
			return dao.getSettings();
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	public String[] getPaths(UriInfo uriInfo)
	{
		try {
			String s = uriInfo.getQueryParameters().getFirst("sorted");
			boolean sorted = ConfigObject.isTrueValue(s);
			return dao.getPaths(sorted);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	public Map<String, FieldInfo> getFieldInfo(UriInfo uriInfo)
	{
		try {
			String param = uriInfo.getQueryParameters().getFirst("fields");
			String[] fields = null;
			if (param != null) {
				fields = param.split(",");
			}
			return dao.getFieldInfo(fields);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

	public boolean isOperatorAllowed(String field, String operator, UriInfo uriInfo)
	{
		try {
			ComparisonOperator op = ComparisonOperator.parse(operator);
			return dao.isOperatorAllowed(field, op);
		}
		catch (Throwable t) {
			throw handleError(uriInfo, t);
		}
	}

}
