package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getQueryResult;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.IScientificNameGroupAccess;
import nl.naturalis.nba.api.InvalidQueryException;
import nl.naturalis.nba.api.QueryResult;
import nl.naturalis.nba.api.QuerySpec;
import nl.naturalis.nba.api.model.ScientificNameGroup;
import nl.naturalis.nba.common.json.JsonUtil;
import nl.naturalis.nba.utils.http.SimpleHttpGet;

public class ScientificNameGroupClient extends NbaClient<ScientificNameGroup>
		implements IScientificNameGroupAccess {

	ScientificNameGroupClient(ClientConfig cfg, String rootPath)
	{
		super(cfg, rootPath);
	}

	@Override
	public QueryResult<ScientificNameGroup> querySpecial(
			QuerySpec querySpec) throws InvalidQueryException
	{
		SimpleHttpGet request = new SimpleHttpGet();
		request.setBaseUrl(config.getBaseUrl());
		request.setPath(rootPath + "querySpecial");
		request.addQueryParam("_querySpec", JsonUtil.toJson(querySpec));
		sendRequest(request);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getQueryResult(request.getResponseBody(), queryResultTypeReference());
	}

	@Override
	Class<ScientificNameGroup> documentObjectClass()
	{
		return ScientificNameGroup.class;
	}

	@Override
	Class<ScientificNameGroup[]> documentObjectArrayClass()
	{
		return ScientificNameGroup[].class;
	}

	@Override
	TypeReference<QueryResult<ScientificNameGroup>> queryResultTypeReference()
	{
		return new TypeReference<QueryResult<ScientificNameGroup>>() {};
	}

}
