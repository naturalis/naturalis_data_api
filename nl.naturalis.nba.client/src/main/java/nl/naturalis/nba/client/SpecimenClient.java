package nl.naturalis.nba.client;

import static org.domainobject.util.http.SimpleHttpRequest.HTTP_NOT_FOUND;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;

class SpecimenClient extends AbstractClient implements ISpecimenAPI {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenClient.class);

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

	@Override
	public Specimen findById(String id)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		setPath("specimen/findByUnitID/" + unitID);
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw NBAResourceException.createFromResponse(status, request.getResponseBody());
		}
		return ClientUtil.getObject(request.getResponseBody(), Specimen[].class);
	}
}
