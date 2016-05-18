package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getBoolean;
import static nl.naturalis.nba.client.ClientUtil.getObject;
import static org.domainobject.util.http.SimpleHttpRequest.HTTP_OK;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.naturalis.nba.api.ISpecimenAPI;
import nl.naturalis.nba.api.model.MultiMediaObject;
import nl.naturalis.nba.api.model.Specimen;
import nl.naturalis.nba.api.query.InvalidQueryException;
import nl.naturalis.nba.api.query.QuerySpec;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.common.json.ObjectMapperLocator;

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
		GET.setPath("specimen/exists/" + unitID);
		int status = GET.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return getBoolean(GET.getResponseBody());
	}

	@Override
	public Specimen find(String id)
	{
		GET.setPath("specimen/find/" + id);
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return getObject(GET.getResponseBody(), Specimen.class);
	}

	@Override
	public Specimen[] findByUnitID(String unitID)
	{
		GET.setPath("specimen/findByUnitID/" + unitID);
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return getObject(GET.getResponseBody(), Specimen[].class);
	}

	@Override
	public Specimen[] query(QuerySpec querySpec) throws InvalidQueryException
	{
		GET.setPath("specimen/query/" + JsonUtil.toJson(querySpec));
		int status = sendGETRequest().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return getObject(GET.getResponseBody(), Specimen[].class);
	}

	public MultiMediaObject[] getMultiMedia(String unitID) throws ServerException
	{
		GET.setPath("specimen/get-multimedia/" + unitID);
		int status = GET.execute().getStatus();
		if (status != HTTP_OK) {
			throw ServerException.createFromResponse(status, GET.getResponseBody());
		}
		return getObject(GET.getResponseBody(), MultiMediaObject[].class);
	}

}
