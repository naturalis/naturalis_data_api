package nl.naturalis.nda.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientException extends Exception {

	private static final long serialVersionUID = -8246486578070786218L;
	private static final ObjectMapper objectMapper = new ObjectMapper();


	@SuppressWarnings("unchecked")
	static ClientException createFromResponse(byte[] response)
	{
		try {
			LinkedHashMap<String, Object> serverInfo = objectMapper.readValue(response, LinkedHashMap.class);
			LinkedHashMap<String, Object> exception = (LinkedHashMap<String, Object>) serverInfo.get("exception");
			String message = (String) exception.get("message");
			return new ClientException(message, serverInfo);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private final Map<String, Object> serverInfo;


	public ClientException(String message, Map<String, Object> serverInfo)
	{
		super(message);
		this.serverInfo = serverInfo;
	}


	public Map<String, Object> getServerInfo()
	{
		return serverInfo;
	}

}
