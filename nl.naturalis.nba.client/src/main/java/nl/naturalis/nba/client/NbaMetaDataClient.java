package nl.naturalis.nba.client;

import static nl.naturalis.nba.client.ClientUtil.getObject;
import static nl.naturalis.nba.client.ServerException.newServerException;
import static nl.naturalis.nba.utils.http.SimpleHttpRequest.HTTP_OK;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;

import nl.naturalis.nba.api.INbaMetaData;
import nl.naturalis.nba.api.model.PhaseOrStage;
import nl.naturalis.nba.api.model.Sex;
import nl.naturalis.nba.api.model.SourceSystem;
import nl.naturalis.nba.api.model.SpecimenTypeStatus;
import nl.naturalis.nba.api.model.TaxonomicStatus;
import nl.naturalis.nba.api.model.metadata.NbaSetting;
import nl.naturalis.nba.utils.http.SimpleHttpRequest;

/**
 * Provides access to NBA-wide metadata. Client-side implementation of
 * {@link INbaMetaData}.
 * 
 * @author Ayco Holleman
 *
 */
public class NbaMetaDataClient extends Client implements INbaMetaData {

	NbaMetaDataClient(ClientConfig config, String rootPath)
	{
		super(config, rootPath);
	}

	@Override
	public Object getSetting(NbaSetting setting)
	{
		SimpleHttpRequest request = getJson("getSetting");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Object.class);
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
	public SourceSystem[] getSourceSystems()
	{
		SimpleHttpRequest request = getJson("getSourceSystems");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), SourceSystem[].class);
	}

	@Override
	public String[] getControlledLists()
	{
		SimpleHttpRequest request = getJson("getControlledLists");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

	@Override
	public PhaseOrStage[] getControlledListPhaseOrStage()
	{
		SimpleHttpRequest request = getJson("getControlledListPhaseOrStage");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), PhaseOrStage[].class);
	}

	@Override
	public SpecimenTypeStatus[] getControlledListSpecimenTypeStatus()
	{
		SimpleHttpRequest request = getJson("getControlledListSpecimenTypeStatus");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), SpecimenTypeStatus[].class);
	}

	@Override
	public Sex[] getControlledListSex()
	{
		SimpleHttpRequest request = getJson("getControlledListSex");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), Sex[].class);
	}

	@Override
	public TaxonomicStatus[] getControlledListTaxonomicStatus()
	{
		SimpleHttpRequest request = getJson("getControlledListTaxonomicStatus");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), TaxonomicStatus[].class);
	}

	@Override
	public String[] getAllowedDateFormats()
	{
		SimpleHttpRequest request = getJson("getAllowedDateFormats");
		int status = request.getStatus();
		if (status != HTTP_OK) {
			throw newServerException(status, request.getResponseBody());
		}
		return getObject(request.getResponseBody(), String[].class);
	}

}
