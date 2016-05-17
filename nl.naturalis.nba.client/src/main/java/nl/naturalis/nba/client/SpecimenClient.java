package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getBoolean;
import static nl.naturalis.nba.client.ClientUtil.getObject;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.domainobject.util.http.SimpleHttpRequest;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;

class SpecimenClient extends AbstractClient implements ISpecimenAPI {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(SpecimenClient.class);

	SpecimenClient(ClientConfig cfg)
	{
		super(cfg);
	}

	@Override
	public boolean exists(String unitID)
	{
		setPath("specimen/exists/" + unitID);
		int status = request.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		return getBoolean(request.getResponseBody());
	}

	public MultiMediaObject[] getMultiMedia(String unitID) throws ServerException
	{
		setPath("specimen/get-multimedia/" + unitID);
		int status = request.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), MultiMediaObject[].class);
	}

	@Override
	public Specimen find(String id)
	{
		setPath("specimen/find/" + id);
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen.class);
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		setPath("specimen/findByUnitID/" + unitID);
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Specimen[].class);
	}

	@Override
	public Specimen[] query(QuerySpec querySpec) throws InvalidQueryException
	{
		setPath("specimen/query");
		request.setContentType(SimpleHttpRequest.MIMETYPE_JSON);
		request.setObject(querySpec);
		return null;
	}
}
