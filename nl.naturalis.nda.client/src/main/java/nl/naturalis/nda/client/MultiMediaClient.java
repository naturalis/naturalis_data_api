package nl.naturalis.nda.client;

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
}
