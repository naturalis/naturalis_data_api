package nl.naturalis.nda.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

class ClientUtil {

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
			return objectMapper.readValue(response, type);
		}
		catch (IOException e) {
			throw new ClientException(e);
		}
	}

}
