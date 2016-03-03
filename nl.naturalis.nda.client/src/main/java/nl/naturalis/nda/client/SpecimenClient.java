package nl.naturalis.nda.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import nl.naturalis.nda.domain.MultiMediaObject;
import nl.naturalis.nda.domain.Specimen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpecimenClient extends AbstractClient {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SpecimenClient.class);

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}

	public boolean exists(String unitID) throws NBAResourceException
	{
		setPath("specimen/exists/" + unitID);
		int status = request.execute().getStatus();
		if (status != HTTP_OK)
			throw NBAResourceException.createFromResponse(status, request.getResponseBody());
		return ClientUtil.getBoolean(request.getResponseBody());
	}
	
	public Specimen find(String unitID) throws NBAResourceException
	{
		setPath("specimen/find/" + unitID);
		int status = request.execute().getStatus();
		if (status == HTTP_NOT_FOUND)
			return null;
		if (status != HTTP_OK)
			throw NBAResourceException.createFromResponse(status, request.getResponseBody());
		return ClientUtil.getObject(request.getResponseBody(), Specimen.class);
	}

	public MultiMediaObject[] getMultiMedia(String unitID) throws NBAResourceException
	{
		setPath("specimen/get-multimedia/" + unitID);
		int status = request.execute().getStatus();
		if (status != HTTP_OK) {
			throw NBAResourceException.createFromResponse(status, request.getResponseBody());
		}
		return ClientUtil.getObject(request.getResponseBody(), MultiMediaObject[].class);
	}
}
