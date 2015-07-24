package nl.naturalis.nda.client;

import java.io.IOException;

import org.domainobject.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

class ClientUtil {

	private static final Logger logger = LoggerFactory.getLogger(ClientUtil.class);
	private static final ObjectMapper objectMapper = NBAObjectMapperFactory.getObjectMapper();


	public static String getString(byte[] response)
	{
		try {
			return objectMapper.readValue(response, String.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}


	public static int getInt(byte[] response)
	{
		try {
			return objectMapper.readValue(response, int.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}


	public static boolean getBoolean(byte[] response)
	{
		try {
			return objectMapper.readValue(response, boolean.class);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}


	public static <T> T getObject(byte[] response, Class<T> type)
	{
		try {
			if (response == null || response.length == 0) {
				return null;
			}
			return objectMapper.readValue(response, type);
		}
		catch (IOException e) {
			logger.error(String.format("Failed to convert NBA response to %s: %s", type, StringUtil.toString(response)));
			throw new ClientException(e);
		}
	}

}
