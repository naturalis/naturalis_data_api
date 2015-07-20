package nl.naturalis.nda.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;
import nl.naturalis.nda.domain.MultiMediaObject;

public class MultiMediaClient extends AbstractClient {

	public MultiMediaClient(ClientConfig cfg)
	{
		super(cfg);
	}


	public boolean exists(String unitID) throws NBAResourceException
	{
		request.setPath("multimedia/exists/" + unitID);
		if (!request.execute().isOK()) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getBoolean(request.getResponseBody());
		}
	}


	public MultiMediaObject find(String unitID) throws NBAResourceException
	{
		request.setPath("multimedia/find/" + unitID);
		int status = request.execute().getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		else if (status != HTTP_OK) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getObject(request.getResponseBody(), MultiMediaObject.class);
		}
	}


	public MultiMediaObject[] getMultiMediaForSpecimen(String specimenUnitID) throws NBAResourceException
	{
		request.setPath("multimedia/get-multimedia-for-specimen/" + specimenUnitID);
		if (!request.execute().isOK()) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getObject(request.getResponseBody(), MultiMediaObject[].class);
		}
	}
}
