package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getBoolean;
import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ClientUtil.getString;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.ArrayUtil.implode;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.ComparisonOperator;
import nl.naturalis.nba.api.INbaDocumentMetaData;
import nl.naturalis.nba.api.NoSuchFieldException;
import nl.naturalis.nba.api.model.IDocumentObject;
import nl.naturalis.nba.api.model.metadata.FieldInfo;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

abstract class NbaDocumentMetaDataClient<T extends IDocumentObject> extends Client
		implements INbaDocumentMetaData<T> {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger(NbaClient.class);

	NbaDocumentMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public Object getSetting(NbaSetting setting)
	{
		String path = "getSetting/" + setting;
		SimpleHttpRequest request = getJson(path);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getString(request.getResponseBody());
	}

	@Override
	public Map<NbaSetting, Object> getSettings()
	{
		SimpleHttpRequest request = getJson("getSettings");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		TypeReference<Map<NbaSetting, Object>> typeRef = new TypeReference<Map<NbaSetting, Object>>() {};
		return getObject(request.getResponseBody(), typeRef);
	}

	@Override
	public String[] getPaths(boolean sorted)
	{
		SimpleHttpRequest request = getJson("getPaths");
		request.addQueryParam("sorted", Boolean.toString(sorted));
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public Map<String, FieldInfo> getFieldInfo(String... fields) throws NoSuchFieldException
	{
		SimpleHttpRequest request = getJson("getFieldInfo");
		if (fields != null && fields.length != 0) {
			request.addQueryParam("fields", implode(fields));
		}
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		TypeReference<Map<String, FieldInfo>> typeRef = new TypeReference<Map<String, FieldInfo>>() {};
		return getObject(request.getResponseBody(), typeRef);
	}

	@Override
	public boolean isOperatorAllowed(String field, ComparisonOperator operator)
	{
		String path = "isOperatorAllowed/" + field + "/" + operator;
		SimpleHttpRequest request = getJson(path);
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getBoolean(request.getResponseBody());
	}

}
