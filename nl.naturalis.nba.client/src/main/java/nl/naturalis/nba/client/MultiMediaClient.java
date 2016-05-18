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
		GET.setPath("multimedia/exists/" + unitID);
		int status = GET.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return ClientUtil.getBoolean(GET.getResponseBody());
	}


	public MultiMediaObject find(String unitID) throws ServerException
	{
		GET.setPath("multimedia/find/" + unitID);
		int status = GET.execute().getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		else if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		else {
			return ClientUtil.getObject(GET.getResponseBody(), MultiMediaObject.class);
		}
	}

}
