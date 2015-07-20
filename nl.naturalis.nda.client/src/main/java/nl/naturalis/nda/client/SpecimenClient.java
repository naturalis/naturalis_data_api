package nl.naturalis.nda.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;
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
		int status = request.execute().getStatus();
		if (status == HTTP_NOT_FOUND) {
			return null;
		}
		else if (status != HTTP_OK) {
			throw NBAResourceException.createFromResponse(request.getResponseBody());
		}
		else {
			return ClientUtil.getObject(request.getResponseBody(), Specimen.class);
		}
	}

}
