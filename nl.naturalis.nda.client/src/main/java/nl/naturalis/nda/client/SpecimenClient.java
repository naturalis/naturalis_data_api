package nl.naturalis.nda.client;

import nl.naturalis.nda.domain.Specimen;

public class SpecimenClient extends AbstractClient {

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}


	public boolean exists(String unitID) throws NBAResourceException
	{
		request.setPath("specimen/exists/" + unitID);
		if (!request.execute().isOK()) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getBoolean(request.getResponseBody());
		}
	}


	public Specimen find(String unitID) throws NBAResourceException
	{
		request.setPath("specimen/find/" + unitID);
		if (!request.execute().isOK()) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getObject(request.getResponseBody(), Specimen.class);
		}
	}

}
