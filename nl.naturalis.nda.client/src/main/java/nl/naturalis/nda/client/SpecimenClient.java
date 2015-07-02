package nl.naturalis.nda.client;


public class SpecimenClient extends AbstractClient {

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}


	public boolean exists(String unitID)
	{
		request.setPath("specimen/exists/" + unitID);
		if (!request.execute().isOK()) {
			ClientException e = ClientException.createFromResponse(request.getResponseBody());
			return false;
		}
		else {
			return false;
		}
	}

}
