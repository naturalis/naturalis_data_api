package nl.naturalis.nda.client;

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
