package nl.naturalis.nba.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import nl.naturalis.nba.api.IMultiMediaObjectAPI;
import nl.naturalis.nba.api.model.MultiMediaObject;

class MultiMediaClient extends AbstractClient implements IMultiMediaObjectAPI {

	MultiMediaClient(ClientConfig cfg)
	{
		super(cfg);
	}


	public boolean exists(String unitID) throws ServerException
	{
		setPath("multimedia/exists/" + unitID);
		int status = request.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		return ClientUtil.getBoolean(request.getResponseBody());
	}


	public MultiMediaObject find(String unitID) throws ServerException
	{
		setPath("multimedia/find/" + unitID);
		int status = request.execute().getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		else if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		else {
			return ClientUtil.getObject(request.getResponseBody(), MultiMediaObject.class);
		}
	}

}
