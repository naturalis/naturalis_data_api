package nl.naturalis.nda.client;

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

}
